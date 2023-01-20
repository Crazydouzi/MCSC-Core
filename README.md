# mc-control-core
用于Minecraft服务器的Web控制台<br/>
懒狗一条= = 慢慢梭哈


## 注解
### @RoutePath
该注解标识在类上，用于标记一个路由

参数
     
     String value()
### @Request
该注解用于标识一个请求

参数:
    
    // 用于标记一个地址
    String value() default "";
    // 请求方法POST、GET等
    HttpMethod[] method() defult HttpMethod.GET;
    // 是否为异步请求
    boolean async() defult true;
请注意，当async为true时Route返回值应当为RoutingContext类型，当async为false时，返回值为JsonObject类型如下：

    @Request(value = "/userUpdate",method = HttpMethod.POST,async = true)
     public RoutingContext userUpdate() {
        return ctx->{
            do something........
            ctx.json(some jsonObject response);
        }；
     }
     @Request(value = "/userLogin",method = HttpMethod.POST)
          public JsonObject userLogin(@RequestBody User user) {
             JsonObject object =new JsonObject();
             do something.......
             return object；
          }
### @RequestBody
用法SpringMVC中的用法一样，标注在参数前(实体类)，此时将自动将相关数据映射到实体类

    @Request(value = "/userUpdate",method = HttpMethod.POST,async = true)
         public RoutingContext userUpdate(@RequestBody User user) {
            return ctx->{
                do something........
                ctx.json(some jsonObject response);
            }；
         }
    
