package com.cdy.simplerpc.remoting.jetty;

import com.cdy.simplerpc.remoting.AbstractServer;
import com.cdy.simplerpc.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import javax.servlet.http.HttpServlet;
import java.util.concurrent.ConcurrentHashMap;

import static com.cdy.simplerpc.util.StringUtil.getServer;

/**
 * http服务端
 * Created by 陈东一
 * 2019/1/27 0027 0:35
 */
@Slf4j
public class HttpServer extends AbstractServer {
    
    public static ConcurrentHashMap<String, HttpServer> servers = new ConcurrentHashMap<>();
    private HttpServlet servlet = new ServletHandler();
    private Server server;
    
    public HttpServer(String address) {
        super(address);
    }
    
    @Override
    public void registerAndListen() throws Exception {
        register();
    
    
        HttpServer rpcServer = servers.get(getAddress());
        if (rpcServer != null) {
            return;
        }
    
        StringUtil.TwoResult<String, Integer> server = getServer(getAddress());
        //维持tomcat服务，否则tomcat一启动就会关闭
        jettyStart(server.getFirst(), server.getSecond());
    }
    
    @Override
    public void close() {
        try {
            server.stop();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
    
    
    public void jettyStart(String host, Integer port) throws Exception {
        server = new Server();// 创建jetty web容器
        server.setStopAtShutdown(true);// 在退出程序是关闭服务
        
        // 创建连接器，每个连接器都是由IP地址和端口号组成，连接到连接器的连接将会被jetty处理
        SelectChannelConnector connector = new SelectChannelConnector();// 创建一个连接器
        connector.setHost(host);// ip地址
        connector.setPort(port);// 连接的端口号
        server.addConnector(connector);// 添加连接
        QueuedThreadPool threadPool = new QueuedThreadPool();
        threadPool.setMaxThreads(3000);
        server.setThreadPool(threadPool);
        // 配置服务
        
        
        
        /*WebAppContext context = new WebAppContext();// 创建服务上下文
        context.setContextPath("/simple-RPC");// 访问服务路径 http://{ip}:{port}/
        context.setConfigurationDiscovered(true);
        context.setHandler(new AbstractHandler(){
            *//**
         * @param target   request的目标，可以是一个url或者一个适配器。
         * @param request  jetty可变的request对象，可以不封装。
         * @param httpServletRequest   不可变的request对象，可以被封装。
         * @param httpServletResponse   response对象，可以被封装
         *//*
            @Override
            public void handle(String target, Request request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {
                servlet.service(httpServletRequest, httpServletResponse);
            }
        });
        String baseDir = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        context.setDescriptor(baseDir + File.separator + "/WEB-INF/web.xml");// 指明服务描述文件，就是web.xml
        // context.setDescriptor("/E:/workspace/strutsDemo/target/classes/\\/WEB-INF/web.xml");//
        // 指明服务描述文件，就是web.xml
        context.setResourceBase(System.getProperty("user.dir") + "/src/main/webapp/");// 指定服务的资源根路径，配置文件的相对路径与服务根路径有关
        server.setHandler(context);// 添加处理*/
        
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
        
        // http://localhost:8080/hello
        context.addServlet(new ServletHolder(servlet), "/**");
        
        server.start();// 开启服务
    
        servers.putIfAbsent(host + ":" + port, this);
//        server.join();
    }
    
    
    
        /*public void tomcatStart(String host, Integer port) throws LifecycleException {
        Tomcat tomcat = new Tomcat();//创建tomcat实例，用来启动tomcat
//        tomcat.setHostname(split[0]);//设置主机名
//        tomcat.setPort(Integer.parseInt(split[1]));//设置端口
        tomcat.setBaseDir("/tomcat/");//tomcat存储自身信息的目录，比如日志等信息，根目录
        
        
        String DEFAULT_PROTOCOL = "org.apache.coyote.http11.Http11NioProtocol";
        Connector connector = new Connector(DEFAULT_PROTOCOL);//设置协议，默认就是这个协议connector.setURIEncoding("UTF-8");//设置编码
        connector.setPort(port);//设置端口
        connector.setURIEncoding("UTF-8");
        tomcat.getService().addConnector(connector);
        
        org.apache.catalina.Context ctx = tomcat.addContext("/simple-RPC", null);//网络访问路径
        tomcat.addServlet(ctx, "myServlet", servlet); //配置servlet
        ctx.addServletMappingDecoded("/**", "myServlet");//配置servlet映射路径
        
        
        StandardServer server = (StandardServer) tomcat.getServer();
        AprLifecycleListener subscrible = new AprLifecycleListener();
        server.addLifecycleListener(subscrible);
        
        //设置appBase为项目所在目录
        
        tomcat.getHost().setAppBase(System.getProperty("user.dir") + File.separator + ".");
        tomcat.addWebapp("", "webapp");
        
        
        tomcat.start();//启动tomcat
        
        tomcat.getServer().await();
    }*/
}
