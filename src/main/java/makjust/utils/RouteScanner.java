package makjust.utils;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.primitives.Primitives;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.ext.web.handler.sockjs.SockJSHandlerOptions;
import javassist.*;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import makjust.annotation.*;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RouteScanner {
    private final ObjectMapper mapper = new ObjectMapper();
    private final Vertx vertx;
    private final SockJSHandlerOptions options = new SockJSHandlerOptions();
    public RouteScanner(Vertx vertx) {
        this.vertx = vertx;
    }

    @SuppressWarnings("unchecked")
    public static <T> T cast(Object obj) {
        return (T) obj;
    }
    public void routerMapping(Object annotatedBean, Router apiRouter, Router wsRouter) throws NotFoundException {
        Class<?> clazz = annotatedBean.getClass();
        if (!clazz.isAnnotationPresent(RoutePath.class)) {
            return;
        }
        RoutePath annotation = clazz.getAnnotation(RoutePath.class);
        String RoutePath = annotation.value();
        ClassPool classPool = ClassPool.getDefault();
        classPool.insertClassPath(new ClassClassPath(clazz));
        CtClass cc = classPool.get(clazz.getName());
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (!((method.isAnnotationPresent(Request.class) || method.isAnnotationPresent(SockJSSocket.class) || method.isAnnotationPresent(WebSocket.class)))) {
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
            // 判断是否为SockJS路由
            if (method.isAnnotationPresent(SockJSSocket.class)) {
                SockJSSocket wsMethodAnno = method.getAnnotation(SockJSSocket.class);
                String path = RoutePath + wsMethodAnno.value();
                String wsPath = (path.startsWith("/") ? path : "/" + path) + "/*";
                System.out.println("webSocket[SockJS]路由地址" + "/ws" + wsPath);
                options.setHeartbeatInterval(2000);
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
                    Router ws = (Router) MethodHandles.lookup().unreflect(method).bindTo(annotatedBean).invokeWithArguments(argValues);
                    wsRouter.route(wsPath).subRouter(ws);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
            // RestFul控制器
            else if (method.isAnnotationPresent(Request.class)) {
                Object[] argValues = new Object[ctMethod.getParameterTypes().length];
                Handler<RoutingContext> requestHandler = ctx -> {
                    try {
                        MultiMap params = ctx.request().params();
                        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
                        List<FileUpload> uploads = ctx.fileUploads();
                        Map<String, FileUpload> uploadMap = uploads.stream().collect(Collectors.toMap(FileUpload::name, x -> x));
                        for (int i = 0; i < argValues.length; i++) {
                            Class<?> paramType = paramTypes[i];
                            JsonData jsonData = null;
                            RequestParam requestParam = null;
                            for (int j = 0; j < parameterAnnotations[i].length; j++) {
                                if (parameterAnnotations[i][j].annotationType() == JsonData.class) {
                                    jsonData = (JsonData) parameterAnnotations[i][j];
                                } else if (parameterAnnotations[i][j].annotationType() == RequestParam.class) {
                                    requestParam = (RequestParam) parameterAnnotations[i][j];
                                }
                            }
                            if (jsonData != null) {
                                JsonObject jsonObject = ctx.body().asJsonObject();
                                Type[] genericParameterTypes = method.getGenericParameterTypes();
                                if (jsonData.value().isEmpty()) {
                                    argValues[i] = parseSimpleTypeOrArrayOrCollection(jsonObject, paramType, paramNames[i], genericParameterTypes[i]);
                                } else {
                                    argValues[i] = parseSimpleTypeOrArrayOrCollection(jsonObject, paramType, jsonData.value(), genericParameterTypes[i]);
                                }
                            } else if (requestParam != null) {
                                if (requestParam.value().isEmpty()) {
                                    //FileType
                                    if (paramType == FileUpload.class) {
                                        argValues[i] = uploadMap.get(requestParam.value());
                                        //Normal Type
                                    } else if (paramType == JsonArray.class || paramType == JsonObject.class || paramType.isArray() || Collection.class.isAssignableFrom(paramType) || isStringOrPrimitiveType(paramType)) {
                                        Type[] genericParameterTypes = method.getGenericParameterTypes();
                                        argValues[i] = parseSimpleTypeOrArrayOrCollection(params, paramType, requestParam.value(), genericParameterTypes[i]);
                                    } else {
                                        argValues[i] = parseBeanType(params, paramType);
                                    }
                                } else {
                                    if (paramType == FileUpload.class) {
                                        argValues[i] = uploadMap.get(paramNames[i]);
                                        //Normal Type
                                    } else if (paramType == JsonArray.class || paramType == JsonObject.class || paramType.isArray() || Collection.class.isAssignableFrom(paramType) || isStringOrPrimitiveType(paramType)) {
                                        Type[] genericParameterTypes = method.getGenericParameterTypes();
                                        argValues[i] = parseSimpleTypeOrArrayOrCollection(params, paramType, paramNames[i], genericParameterTypes[i]);
                                    } else {
                                        argValues[i] = parseBeanType(params, paramType);
                                    }
                                }
                            }
                            // special type
                            else if (paramType == RoutingContext.class) {
                                argValues[i] = ctx;
                            } else if (paramType == Vertx.class) {
                                argValues[i] = vertx;
                            } else {
                                argValues[i] = null;
                            }
                        }
                        //异步处理
                        if (method.getAnnotation(Request.class).async()) {
                            ctx = (RoutingContext) MethodHandles.lookup().unreflect(method).bindTo(annotatedBean).invokeWithArguments(argValues);
                            //同步处理
                        } else {
                            Object result = MethodHandles.lookup().unreflect(method).bindTo(annotatedBean).invokeWithArguments(argValues);
                            // 返回Json类型结果集
                            ctx.json(result);
                        }

                    } catch (Throwable e) {
                        e.printStackTrace();
                        HashMap<String, Object> result = new HashMap<>();
                        result.put("message", "system error");
                        ctx.response().setStatusCode(500).end(Json.encode(result));
                    }
                };

                Request methodAnno = method.getAnnotation(Request.class);
                String requestPath = RoutePath + methodAnno.value();
                String formatPath = requestPath.startsWith("/") ? requestPath : "/" + requestPath;
                System.out.println("API路由地址：" + "/api" + formatPath);
                // bind handler to apiRouter
                if (methodAnno.method().length == 0) {
                    // 默认绑定全部HttpMethod
                    apiRouter.route(formatPath).handler(BodyHandler.create()).handler(requestHandler);
                } else {
                    for (makjust.annotation.HttpMethod m : methodAnno.method()) {
                        apiRouter.route(io.vertx.core.http.HttpMethod.valueOf(String.valueOf(m)), formatPath).handler(BodyHandler.create()).handler(requestHandler);
                    }
                }
            } else {
                System.out.println("路由挂载失败");
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
    private Object parseSimpleTypeOrArrayOrCollection(MultiMap allParams, Class<?> paramType, String
            paramName, Type genericParameterTypes) throws Throwable {
        if (allParams.get(paramName) == null) {
            return null;
        } else if (paramType.isArray() || Collection.class.isAssignableFrom(paramType)) {

            JavaType javaType = mapper.getTypeFactory().constructType(genericParameterTypes);
            return mapper.readValue(allParams.get(paramName), javaType);
        }
        // Json Type
        else if (paramType == JsonObject.class || paramType == JsonArray.class) {
            return Json.decodeValue(allParams.get(paramName));
        }
        // String and primitive type
        else if (isStringOrPrimitiveType(paramType)) {
            return parseSimpleType(allParams.get(paramName), paramType);
        } else {
            return null;
        }
    }

    /**
     * 解析简单类型以及对应的集合或数组类型
     *
     * @param object    Json数据
     * @param paramType 参数类型
     * @param paramName 参数名称
     */
    private Object parseSimpleTypeOrArrayOrCollection(JsonObject object, Class<?> paramType, String
            paramName, Type genericParameterTypes) {
        try {
            if (object.getValue(paramName) == null) {
                System.out.println(11);
                if (paramType == JsonArray.class || paramType == JsonObject.class || paramType.isArray() || Collection.class.isAssignableFrom(paramType) || isStringOrPrimitiveType(paramType)){
                    return null;
                }else {
                    return object.mapTo(paramType);
                }
            }
            // Array|Collection type
            else if (paramType.isArray() || Collection.class.isAssignableFrom(paramType)) {
                JavaType javaType = mapper.getTypeFactory().constructType(genericParameterTypes);
                return mapper.readValue(object.getValue(paramName).toString(), javaType);
            }
            // String and primitive type
            else if (isStringOrPrimitiveType(paramType)) {
                return parseSimpleType(object.getString(paramName), paramType);
            } else if (paramType == JsonArray.class) {
                return object.getJsonArray(paramName);
            } else if (paramType == JsonObject.class) {
                return object.getJsonObject(paramName);
            } else {
                return Json.decodeValue(object.getValue(paramName).toString(), paramType);
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        }
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
     * 解析实体对象
     *
     * @param allParams 所有参数
     * @param paramType 实体参数类型
     * @return 已经注入字段的实体对象
     */
    private Object parseBeanType(MultiMap allParams, Class<?> paramType) throws Throwable {
        Object bean = paramType.getDeclaredConstructor().newInstance();
        Field[] fields = paramType.getDeclaredFields();
        Object value;
        for (Field field : fields) {
            value = parseSimpleTypeOrArrayOrCollection(allParams, field.getType(), field.getName(), field.getGenericType());
            field.setAccessible(true);
            field.set(bean, value);
        }
        return bean;
    }
}
