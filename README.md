# simple-rpc   rpc框架的实现



2019年8月21日 搭建基础框架

- annotation  注解包
- balance     负载均衡相关
- config      外部化配置,以及远程配置(zk, nacos)
- event       事件驱动（未应用完全）
- exception   自定义异常
- filter      过滤器     
- monitor     监控(待完成)
- proxy       代理以及具体的执行器invoker
- registry    注册(zookeeper，nacos)
- remoting    远程调用(netty, jetty)
- route       路由（未应用完全）
- rpc         rpc模块
- serialize   序列化模块
- util        工具包


服务端通过ServerBootStrap引导类配置启动, 主要配置服务发现的实现类, 等待可以端调用, 具体见ServerTest测试类
客户端通过ClientBootStrap引导类配置启动, 主要配置服务注册, 负载均衡等实现类, 然后通过动态代理生成远程调用的invoker, 具体见ClientTest测试类

todo
spring整合
一些待完善的内容