# simple-rpc   rpc框架的实现



2018-11-25 搭建基础框架

- annotation  注解包
- balance     负载均衡相关
- config      配置
- exception   自定义异常
- filter      过滤器     
- monitor     监控(待完成)
- proxy       代理以及具体的执行器invoker
- registry    注册(zookeeper)
- remoting    远程调用(netty, jetty)
- test        测试用
- util        工具包


服务端通过ServerBootStrap引导类配置启动, 主要配置服务发现的实现类, 等待可以端调用, 具体见ServerTest测试类
客户端通过ClientBootStrap引导类配置启动, 主要配置服务注册, 负载均衡等实现类, 然后通过动态代理生成远程调用的invoker, 具体见ClientTest测试类

