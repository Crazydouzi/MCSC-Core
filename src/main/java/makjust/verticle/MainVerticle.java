package makjust.verticle;

import com.google.common.primitives.Primitives;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.Json;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.ext.web.handler.sockjs.SockJSHandlerOptions;
import javassist.*;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import makjust.annotation.Controller;
import makjust.annotation.RequestBody;
import makjust.annotation.RequestMapping;
import makjust.annotation.Socket;
import makjust.utils.ClassScanUtil;
import makjust.utils.getConfig;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Modifier;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

public class MainVerticle extends AbstractVerticle {
    @Override
    public void start() throws Exception {
        // 主路由
        Router router = Router.router(vertx);
        //api子路由
        Router apiRouter = Router.router(vertx);
        // ws子路由(SockJs)
        Router wsRouter = Router.router(vertx);
        // 自动加载控制器路由
        Set<Class<?>> classes = ClassScanUtil.scanByAnnotation("makjust.controller", Controller.class);
        for (Class<?> cls : classes) {
            Object controller = cls.getConstructor().newInstance();
            routerMapping(controller, apiRouter, wsRouter);
        }

        // 静态资源路由
        if (getConfig.getCoreConf().getBoolean("enWeb")) {
            router.route().handler(StaticHandler.create().setWebRoot(getConfig.getStaticPath()));
        }
        //挂载子路由
        router.route("/api/*").consumes("*/json").handler(BodyHandler.create()).subRouter(apiRouter);
        router.route("/ws/*").subRouter(wsRouter);
        vertx.createHttpServer().requestHandler(router).listen(8080);

    }

    private <ControllerType> void routerMapping(ControllerType annotatedBean, Router router, Router wsRouter) throws NotFoundException {
        Class<ControllerType> clazz = (Class<ControllerType>) annotatedBean.getClass();
        if (!clazz.isAnnotationPresent(Controller.class)) {
            return;
        }
        Controller annotation = clazz.getAnnotation(Controller.class);
        String ControllerPath = annotation.value();
        ClassPool classPool = ClassPool.getDefault();
        classPool.insertClassPath(new ClassClassPath(clazz));
        CtClass cc = classPool.get(clazz.getName());
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (!((method.isAnnotationPresent(RequestMapping.class) || method.isAnnotationPresent(Socket.class)))) {
                continue;
            }
            CtMethod ctMethod = cc.getDeclaredMethod(method.getName());
            MethodInfo methodInfo = ctMethod.getMethodInfo();
            CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
            LocalVariableAttribute attribute = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
            Class<?>[] paramTypes = method.getParameterTypes();
            String[] paramNames = new String[ctMethod.getParameterTypes().length];
            if (attribute != null) {
                // 通过javassist获取方法形参，成员方法 0位变量是this
                int pos = Modifier.isStatic(ctMethod.getModifiers()) ? 0 : 1;
                for (int i = 0; i < paramNames.length; i++) {
                    paramNames[i] = attribute.variableName(i + pos);
                }
            }


            if (method.isAnnotationPresent(Socket.class)) {
                Socket wsMethodAnno = method.getAnnotation(Socket.class);
                String path = ControllerPath + wsMethodAnno.value();
                String wsPath = (path.startsWith("/") ? path : "/" + path) + "/*";
                System.out.println(wsPath);
                SockJSHandlerOptions options = new SockJSHandlerOptions();
                SockJSHandler sockJSHandler = SockJSHandler.create(vertx, options);
                Object[] argValues = new Object[ctMethod.getParameterTypes().length];
                for (int i = 0; i < argValues.length; i++) {
                    Class<?> paramType = paramTypes[i];
                    if (paramType == Vertx.class) {
                        argValues[i] = vertx;
                    } else if (paramType == SockJSHandler.class) {
                        argValues[i] = sockJSHandler;
                    } else if (paramType == Router.class) {
                        argValues[i] = wsRouter;
                    }
                }
                try {
                    System.out.println(Arrays.toString(argValues));
                    Router ws = (Router) MethodHandles.lookup().unreflect(method).bindTo(annotatedBean).invokeWithArguments(argValues);
                    wsRouter.route(wsPath).subRouter(ws);
                    continue;
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }

            if (method.isAnnotationPresent(RequestMapping.class)) {
                Handler<RoutingContext> requestHandler = ctx -> {
                    try {
                        Object[] argValues = new Object[ctMethod.getParameterTypes().length];
                        MultiMap params = ctx.request().params();
                        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
                        List<FileUpload> uploads = ctx.fileUploads();
                        Map<String, FileUpload> uploadMap = uploads.stream().collect(Collectors.toMap(FileUpload::name, x -> x));
                        for (int i = 0; i < argValues.length; i++) {
                            Class<?> paramType = paramTypes[i];
                            // @RequestBody数据解析
                            List<? extends Class<? extends Annotation>> parameterAnnotation = Arrays.stream(parameterAnnotations[i]).map(Annotation::annotationType).collect(Collectors.toList());
                            if (parameterAnnotation.contains(RequestBody.class)) {
                                String bodyAsString = ctx.body().asString();
                                argValues[i] = Json.decodeValue(bodyAsString, paramType);
                            }
                            // special type
                            else if (paramType == RoutingContext.class) {
                                argValues[i] = ctx;
                            } else if (paramType == FileUpload.class) {
                                argValues[i] = uploadMap.get(paramNames[i]);
                            }
                            // Normal Type
                            else if (paramType.isArray() || Collection.class.isAssignableFrom(paramType) || isStringOrPrimitiveType(paramType)) {
                                Type[] genericParameterTypes = method.getGenericParameterTypes();
                                argValues[i] = parseSimpleTypeOrArrayOrCollection(params, paramType, paramNames[i], genericParameterTypes[i]);
                            }
                            // POJO Bean
                            else {
                                argValues[i] = parseBeanType(params, paramType);
                            }
                        }
                        Object result = MethodHandles.lookup().unreflect(method).bindTo(annotatedBean).invokeWithArguments(argValues);
                        // 返回Json类型结果集
                        ctx.json(result);
                    } catch (Throwable e) {
                        HashMap<String, Object> result = new HashMap<>();
                        result.put("message", "system error");
                        ctx.response().end(Json.encode(result));
                        e.printStackTrace();
                    }
                };
                RequestMapping methodAnno = method.getAnnotation(RequestMapping.class);
                String requestPath = ControllerPath + methodAnno.value();
                String formatPath = requestPath.startsWith("/") ? requestPath : "/" + requestPath;
                // bind handler to router
                if (methodAnno.method().length == 0) {
                    // 默认绑定全部HttpMethod
                    router.route(formatPath).handler(BodyHandler.create()).handler(requestHandler);
                } else {
                    for (makjust.annotation.HttpMethod m : methodAnno.method()) {
                        router.route(HttpMethod.valueOf(String.valueOf(m)), formatPath).handler(BodyHandler.create()).handler(requestHandler);
                    }
                }
            }

        }
    }

    /**
     * 解析简单类型以及对应的集合或数组类型
     *
     * @param allParams             所有请求参数
     * @param paramType             参数类型
     * @param paramName             参数名称
     * @param genericParameterTypes 泛型化参数类型
     */
    private Object parseSimpleTypeOrArrayOrCollection(MultiMap allParams, Class<?> paramType, String paramName, Type genericParameterTypes) throws Throwable {
        // Array type
        if (paramType.isArray()) {
            // 数组元素类型
            Class<?> componentType = paramType.getComponentType();

            List<String> values = allParams.getAll(paramName);
            Object array = Array.newInstance(componentType, values.size());
            for (int j = 0; j < values.size(); j++) {
                Array.set(array, j, parseSimpleType(values.get(j), componentType));
            }
            return array;
        }
        // Collection type
        else if (Collection.class.isAssignableFrom(paramType)) {
            return parseCollectionType(allParams.getAll(paramName), genericParameterTypes);
        }
        // String and primitive type
        else if (isStringOrPrimitiveType(paramType)) {
            return parseSimpleType(allParams.get(paramName), paramType);
        }

        return null;
    }

    /**
     * 判断是否为字符串或基础类型以及对应的包装类型
     */
    private boolean isStringOrPrimitiveType(Class<?> targetClass) {
        return targetClass == String.class || Primitives.allWrapperTypes().contains(Primitives.wrap(targetClass));
    }

    /**
     * 处理字符串，基础类型以及对应的包装类型
     */
    @SuppressWarnings("unchecked")
    private <T> T parseSimpleType(String value, Class<T> targetClass) throws Throwable {
        if (StringUtils.isBlank(value)) {
            return null;
        }

        Class<?> wrapType = Primitives.wrap(targetClass);
        if (Primitives.allWrapperTypes().contains(wrapType)) {
            MethodHandle valueOf = MethodHandles.lookup().unreflect(wrapType.getMethod("valueOf", String.class));
            return (T) valueOf.invoke(value);
        } else if (targetClass == String.class) {
            return (T) value;
        }

        return null;
    }

    /**
     * 解析集合类型
     *
     * @param values               请求参数值
     * @param genericParameterType from Method::getGenericParameterTypes
     */
    private Collection parseCollectionType(List<String> values, Type genericParameterType) throws Throwable {
        Class<?> actualTypeArgument = String.class; // 无泛型参数默认用String类型
        Class<?> rawType;
        // 参数带泛型
        if (genericParameterType instanceof ParameterizedType) {
            ParameterizedType parameterType = (ParameterizedType) genericParameterType;
            actualTypeArgument = (Class<?>) parameterType.getActualTypeArguments()[0];
            rawType = (Class<?>) parameterType.getRawType();
        } else {
            rawType = (Class<?>) genericParameterType;
        }

        Collection coll;
        if (rawType == List.class) {
            coll = new ArrayList<>();
        } else if (rawType == Set.class) {
            coll = new HashSet<>();
        } else {
            coll = (Collection) rawType.newInstance();
        }

        for (String value : values) {
            coll.add(parseSimpleType(value, actualTypeArgument));
        }
        return coll;
    }

    /**
     * 解析实体对象
     *
     * @param allParams 所有参数
     * @param paramType 实体参数类型
     * @return 已经注入字段的实体对象
     */
    private Object parseBeanType(MultiMap allParams, Class<?> paramType) throws Throwable {
        Object bean = paramType.newInstance();
        Field[] fields = paramType.getDeclaredFields();
        for (Field field : fields) {
            Object value = parseSimpleTypeOrArrayOrCollection(allParams, field.getType(), field.getName(), field.getGenericType());

            field.setAccessible(true);
            field.set(bean, value);
        }
        return bean;
    }
}
