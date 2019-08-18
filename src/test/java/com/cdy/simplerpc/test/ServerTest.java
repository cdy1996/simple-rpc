package com.cdy.simplerpc.test;

import com.cdy.simplerpc.ServerBootStrap;
import com.cdy.simplerpc.annotation.RPCService;
import com.cdy.simplerpc.config.AnnotationPropertySource;
import com.cdy.simplerpc.config.LocalPropertySource;
import com.cdy.simplerpc.config.PropertySources;
import com.cdy.simplerpc.registry.IServiceRegistry;
import com.cdy.simplerpc.registry.RegistryFactory;
import com.cdy.simplerpc.remoting.Server;
import com.cdy.simplerpc.remoting.ServerFactory;
import org.junit.Test;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 服务端测试
 * Created by 陈东一
 * 2019/1/22 0022 22:28
 */
public class ServerTest {
    
    /**
     * 外部化配置顺序
     * 文件配置
     * 注解配置
     * 系统配置
     * 环境配置
     */
    
    @Test
    public void nativeTest() throws Exception {
        //外部化配置
        PropertySources propertySources = new PropertySources();
        propertySources.addPropertySources(new LocalPropertySource("D:\\workspace\\ideaworkspace\\blog_project\\simple-rpc\\src\\main\\resources\\simlpe-rpc.properties"));
        
        //扫描注解属性
        TestServiceImpl object = new TestServiceImpl();
        RPCService annotation = object.getClass().getAnnotation(RPCService.class);
        Map<String, String> config = RPCService.ServiceAnnotationInfo.getConfig(object.getClass().getSimpleName(), annotation);
        propertySources.addPropertySources(new AnnotationPropertySource(config));
        
        // 多注册中心
        String types = propertySources.resolveProperty("registry.types");
        List<IServiceRegistry> serviceRegistryList = Arrays.stream(types.split(",")).map(type -> RegistryFactory.createRegistry(propertySources, type)).collect(Collectors.toList());
        
        
        //多协议
        for (String protocol : annotation.protocols()) {
            Server server = ServerFactory.createServer(serviceRegistryList, propertySources, protocol);
            String serviceName = object.getClass().getName();
            server.bind(serviceName, object, Collections.emptyList());
            server.openServer();
            server.register(serviceName);
            
        }
        
        
        System.in.read();
        
    }
    
    @Test
    public void boostStrapTest() throws Exception {
//        PropertySources propertySources = new PropertySources();
//        propertySources.addPropertySources(new LocalPropertySource("D:\\workspace\\ideaworkspace\\blog_project\\simple-rpc\\src\\main\\resources\\simlpe-rpc.properties"));
    
        ServerBootStrap rpc = ServerBootStrap
                .build(new TestServiceImpl(), "D:\\workspace\\ideaworkspace\\blog_project\\simple-rpc\\src\\main\\resources\\simlpe-rpc.properties")
//                .registry("nacos-1", "127.0.0.1:8848", "529469ac-0341-4276-a256-14dcf863935c")
//                .registry("zookeeper-1", "127.0.0.1:2181", "/registry")
                .port("8080")
                .ip("127.0.0.1")
//                .protocols("http")
                .protocols("rpc")
                .start();
    
        System.in.read();
        rpc.closeAll();
    }
    
    @Test
    public void test() throws Exception {
        System.setProperty("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");
    
        Annotation[] annotations = LocalPropertySource.class.getAnnotations();
        System.out.println("!23");
    }
    

    
}
//