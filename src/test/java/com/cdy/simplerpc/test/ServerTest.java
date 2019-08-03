package com.cdy.simplerpc.test;

import com.cdy.simplerpc.config.LocalPropertySource;
import com.cdy.simplerpc.config.PropertySources;
import com.cdy.simplerpc.registry.IServiceRegistry;
import com.cdy.simplerpc.registry.nacos.NacosRegistry;
import com.cdy.simplerpc.remoting.Server;
import com.cdy.simplerpc.remoting.ServerFactory;
import org.junit.Test;

import java.util.Collections;

/**
 * 服务端测试
 * Created by 陈东一
 * 2019/1/22 0022 22:28
 */
public class ServerTest {
    
    @Test
    public void nativeTest() throws Exception {
        
        PropertySources propertySources = new PropertySources();
        propertySources.addPropertySources(new LocalPropertySource("D:\\workspace\\ideaworkspace\\blog_project\\simple-rpc\\src\\main\\resources\\simlpe-rpc.properties"));
        
        IServiceRegistry registery = new NacosRegistry(propertySources);
        
        TestServiceImpl object = new TestServiceImpl();
        try (Server server = ServerFactory.createServer(registery, "rpc", "8080", "127.0.0.1")){
        String serviceName = object.getClass().getName();
        server.bind(serviceName, object, Collections.EMPTY_LIST, null);
        server.openServer();
        server.register(serviceName);
        System.in.read();
    }
    
}
    
//    @Test
//    public void mutlTest() throws Exception {
//        ServerBootStrap serverBootStrap = new ServerBootStrap();
//
//        IServiceRegistry registery = new SimpleRegisteryImpl();
//        serverBootStrap.setRegistry(registery);
//
//        TestServiceImpl object = new TestServiceImpl();
//        serverBootStrap.bind("rpc", "8080", Collections.EMPTY_LIST, object);
//        serverBootStrap.bind("rpc", "8082", Collections.EMPTY_LIST, object);
//        serverBootStrap.bind("http", "8888", Collections.EMPTY_LIST, object);
//        System.in.read();
//        serverBootStrap.closeAll();
//
//    }
//
//    @Test
//    public void nacosTest() throws Exception {
//        ServerBootStrap serverBootStrap = new ServerBootStrap();
//
//        PropertySources propertySources = new PropertySources();
//        propertySources.addPropertySources(new LocalPropertySource("D:\\workspace\\ideaworkspace\\blog_project\\simple-rpc\\src\\main\\resources\\simlpe-rpc.properties"));
//
//        IServiceRegistry registery = new NacosRegistry(propertySources);
//        serverBootStrap.setRegistry(registery);
//
//        TestServiceImpl object = new TestServiceImpl();
//        serverBootStrap.bind("rpc", "8080", Collections.EMPTY_LIST, object);
//        System.in.read();
//        serverBootStrap.closeAll();
//    }
    
}
