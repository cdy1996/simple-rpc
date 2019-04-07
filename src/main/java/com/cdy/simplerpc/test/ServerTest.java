package com.cdy.simplerpc.test;

import com.cdy.simplerpc.ServerBootStrap;
import com.cdy.simplerpc.registry.zookeeper.ZKServiceRegistryImpl;
import com.cdy.simplerpc.remoting.netty.RPCServer;

/**
 * 服务端测试
 * Created by 陈东一
 * 2019/1/22 0022 22:28
 */
public class ServerTest {
    
    public static void main(String[] args) throws Exception {
        ServerBootStrap serverBootStrap = new ServerBootStrap();
        serverBootStrap.setRegistry((new ZKServiceRegistryImpl()));
        serverBootStrap.setServer(new RPCServer("127.0.0.1:8080"));
        
        serverBootStrap.bind(new TestServiceImpl());
        System.in.read();
        
    }
    
}
