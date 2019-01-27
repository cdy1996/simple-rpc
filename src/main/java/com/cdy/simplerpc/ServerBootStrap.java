package com.cdy.simplerpc;

import com.cdy.simplerpc.config.RegistryConfig;
import com.cdy.simplerpc.config.RemotingConfig;
import com.cdy.simplerpc.proxy.Invoker;
import com.cdy.simplerpc.registry.IServiceRegistry;
import com.cdy.simplerpc.registry.simple.SimpleRegisteryImpl;
import com.cdy.simplerpc.remoting.Server;
import com.cdy.simplerpc.remoting.netty.RPCServer;

import java.util.function.Function;

/**
 * todo
 * Created by 陈东一
 * 2019/1/22 0022 22:11
 */
public class ServerBootStrap {
    
    private IServiceRegistry registry;
    
    private Server server;
    

    public ServerBootStrap start(RegistryConfig registryConfig, RemotingConfig remotingConfig){
    
        IServiceRegistry registry = new SimpleRegisteryImpl();
        this.registry = registry;
        
        Server server = new RPCServer(registry, "127.0.0.1:8899");
        this.server = server;
        return this;
    }
    
    public void bind(Object object, Function<Invoker, Invoker> ... function){
        server.bind(object, function);
        server.registerAndListen();
    }
}
