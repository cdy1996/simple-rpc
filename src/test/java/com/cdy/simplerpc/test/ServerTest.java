package com.cdy.simplerpc.test;

import com.cdy.simplerpc.ServerBootStrap;
import com.cdy.simplerpc.registry.IServiceRegistry;
import com.cdy.simplerpc.registry.simple.SimpleRegisteryImpl;
import com.cdy.simplerpc.remoting.Server;
import com.cdy.simplerpc.remoting.http.HttpServer;
import com.cdy.simplerpc.remoting.rpc.RPCServer;

import java.util.Collections;

/**
 * 服务端测试
 * Created by 陈东一
 * 2019/1/22 0022 22:28
 */
public class ServerTest {
    
    public static void main(String[] args) throws Exception {
        ServerBootStrap serverBootStrap = new ServerBootStrap();
//        ZKServiceRegistryImpl registery = new ZKServiceRegistryImpl();
        
        IServiceRegistry registery = new SimpleRegisteryImpl();
        Server rpcServer = new RPCServer("rpc-127.0.0.1:8080");
        Server rpcServer2 = new RPCServer("rpc-127.0.0.1:8082");
        
        Server httpServer = new HttpServer("http-127.0.0.1:8888");
        rpcServer.setRegistry(registery);
        rpcServer2.setRegistry(registery);
        httpServer.setRegistry(registery);
    
        TestServiceImpl object = new TestServiceImpl();
        serverBootStrap.bind(rpcServer, Collections.EMPTY_LIST, object);
        serverBootStrap.bind(rpcServer2, Collections.EMPTY_LIST, object);
        serverBootStrap.bind(httpServer, Collections.EMPTY_LIST, object);
        System.in.read();
        serverBootStrap.closeAll();
        
    }
    
}
