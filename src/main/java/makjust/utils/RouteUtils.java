package makjust.utils;

import com.google.common.primitives.Primitives;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.ext.web.handler.sockjs.SockJSHandlerOptions;
import io.vertx.ext.web.sstore.SessionStore;
import javassist.*;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import makjust.annotation.*;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Modifier;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

public class RouteUtils {
    private final Vertx vertx;
    private final Router apiRouter;
    private final Router wsRouter;
    private final Router router;
    private final SockJSHandlerOptions options = new SockJSHandlerOptions();
    private final HttpServer server;

    public RouteUtils(Vertx vertx) {
        this.vertx = vertx;
        //主路由
        this.router = Router.router(vertx);
        //api子路由
        this.apiRouter = Router.router(vertx);
        // ws子路由(SockJs)
        this.wsRouter = Router.router(vertx);
        this.server = vertx.createHttpServer();

    }

    public void scanRoute(String scanPath) {
        try {
            Set<Class<?>> classes = ClassScanUtil.scanByAnnotation(scanPath, RoutePath.class);
            for (Class<?> cls : classes) {
                Object controller = cls.getConstructor().newInstance();
                routerMapping(controller, apiRouter, wsRouter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 默认方法 指定文件夹
     */
    public void setStaticRoute(String webRootLoc) {
        router.route().method(HttpMethod.GET)
                .handler(
                        StaticHandler
                                .create(webRootLoc)
                                .setCachingEnabled(false)
                                .setDefaultContentEncoding("utf-8"));
    }

    public void setStaticRoute(String webRootLOC, String excludePathRegex) {
        router.errorHandler(404, ctx -> {
            if (ctx.request().method() == HttpMethod.GET && ctx.request().uri().matches(excludePathRegex)) {
                ctx.reroute("/index.html");
            } else {
                ctx.json(new JsonObject().put("code", 404).put("msg", "页面走丢了哟~"));
            }
        });
        router.route().pathRegex(excludePathRegex).method(HttpMethod.GET)
                .handler(StaticHandler.create(webRootLOC).setCachingEnabled(false).setDefaultContentEncoding("utf-8"));
    }

    public void setVueRouteEnable(String apiRouteRegex) {
        router.errorHandler(404, ctx -> {
            if (ctx.request().method() == HttpMethod.GET && ctx.request().uri().matches(apiRouteRegex)) {
                ctx.reroute("/index.html");
            } else {
                ctx.json(new JsonObject().put("code", 404).put("msg", "页面走丢了哟~"));
            }
        });
    }

    /**
     * 挂载子路由到主路由
     * 例如："/ws/*"”
     *
     * @param apiPathPrefix apiURL前缀
     * @param wsPathPrefix  wsURL前缀
     */
    public void mountAllRoute(String apiPathPrefix, String wsPathPrefix) {
        mountWSRoute(wsPathPrefix);
        mountAPIRoute(apiPathPrefix);
    }

    public void enableCORS() {
        Set<String> allowedHeaders = new HashSet<>();
        allowedHeaders.add("x-requested-with");
        allowedHeaders.add("Access-Control-Allow-Origin");
        allowedHeaders.add("Access-Control-Request-Headers");
        allowedHeaders.add("origin");
        allowedHeaders.add("Content-Type");
        allowedHeaders.add("accept");
        allowedHeaders.add("X-PINGARUNER");

        Set<HttpMethod> allowedMethods = new HashSet<>();
        allowedMethods.add(HttpMethod.GET);
        allowedMethods.add(HttpMethod.POST);
        allowedMethods.add(HttpMethod.OPTIONS);
        /*
         * these methods aren't necessary for this sample,
         * but you may need them for your projects
         */
        allowedMethods.add(HttpMethod.DELETE);
        allowedMethods.add(HttpMethod.PATCH);
        allowedMethods.add(HttpMethod.PUT);
        allowedMethods.add(HttpMethod.HEAD);
        router.route().handler(CorsHandler.create()
                .allowedHeaders(allowedHeaders)
                .allowedMethods(allowedMethods)
        );
    }

    public void enableSockJSCORS() {
        Set<String> exposedHeaders = new HashSet<>();
        exposedHeaders.add("Access-Control-Allow-Headers");
        exposedHeaders.add("Access-Control-Allow-Method");
        exposedHeaders.add("Access-Control-Max-Age");
        exposedHeaders.add("Access-Control-Request-Headers");
        exposedHeaders.add("X-Frame-Options");
        wsRouter.route().handler(CorsHandler.create().allowedHeader("Content-Type").exposedHeaders(exposedHeaders).allowCredentials(true));
    }

    public void mountAPIRoute(String URLPrefix) {
        router.route(URLPrefix).consumes("*/json").consumes("multipart/form-data").consumes("application/x-www-form-urlencoded").handler(BodyHandler.create()).subRouter(apiRouter);
    }

    public void mountWSRoute(String URLPrefix) {
        router.route(URLPrefix).subRouter(wsRouter);
    }

    public void createLocalSession() {
        SessionStore store = SessionStore.create(vertx);
        SessionHandler sessionHandler = SessionHandler.create(store);
        router.route().handler(sessionHandler);
    }

    public void startHttpServer(int port) {
        server.requestHandler(getRouter()).listen(port);

    }

    // this is a default port ,it will use port 8080;
    public void startHttpServer() {
        startHttpServer(8080);
    }

    //装载到子路由
    private void routerMapping(Object annotatedBean, Router apiRouter, Router wsRouter) throws NotFoundException {
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
                            // @RequestBody数据解析
                            List<? extends Class<? extends Annotation>> parameterAnnotation = Arrays.stream(parameterAnnotations[i]).map(Annotation::annotationType).collect(Collectors.toList());
                            if (parameterAnnotation.contains(RequestBody.class)) {
                                String bodyAsString = ctx.body().asString();
                                if (bodyAsString == null) {
                                    argValues[i] = null;
                                } else {
                                    argValues[i] = Json.decodeValue(bodyAsString, paramType);
                                }
                            }
                            // special type
                            else if (paramType == RoutingContext.class) {
                                argValues[i] = ctx;
                            } else if (paramType == Vertx.class) {
                                argValues[i] = vertx;
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
                        apiRouter.route(HttpMethod.valueOf(String.valueOf(m)), formatPath).handler(BodyHandler.create()).handler(requestHandler);
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
    private Collection<Object> parseCollectionType(List<String> values, Type genericParameterType) throws
            Throwable {
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

        Collection<Object> coll;
        if (rawType == List.class) {
            coll = new ArrayList<>();
        } else if (rawType == Set.class) {
            coll = new HashSet<>();
        } else {
            coll = cast(rawType.getDeclaredConstructor().newInstance());
//            coll = (Collection<Object>) rawType.getDeclaredConstructor().newInstance();
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
        Object bean = paramType.getDeclaredConstructor().newInstance();
        Field[] fields = paramType.getDeclaredFields();
        for (Field field : fields) {
            Object value = parseSimpleTypeOrArrayOrCollection(allParams, field.getType(), field.getName(), field.getGenericType());

            field.setAccessible(true);
            field.set(bean, value);
        }
        return bean;
    }

    @SuppressWarnings("unchecked")
    public static <T> T cast(Object obj) {
        return (T) obj;
    }

    public Router getRouter() {
        return router;
    }
}
