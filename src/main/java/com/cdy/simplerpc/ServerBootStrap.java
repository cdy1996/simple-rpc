package com.cdy.simplerpc;

import com.cdy.simplerpc.filter.Filter;
import com.cdy.simplerpc.proxy.Invoker;
import com.cdy.simplerpc.registry.IServiceRegistry;
import com.cdy.simplerpc.registry.simple.SimpleRegisteryImpl;
import com.cdy.simplerpc.remoting.Server;
import com.cdy.simplerpc.remoting.netty.RPCServer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * 服务端启动类
 * Created by 陈东一
 * 2019/1/22 0022 22:11
 */
public class ServerBootStrap {
    
    private IServiceRegistry registry = new SimpleRegisteryImpl();
    
    private Server server = new RPCServer("127.0.0.1:8899");
    
    private List<Filter> filters = new ArrayList<>();
    
    public static ServerBootStrap build() {
        return new ServerBootStrap();
    }
    
    public void setRegistry(IServiceRegistry registry) {
        this.registry = registry;
    }
    
    public void setServer(Server server) {
        server.setRegistry(registry);
        this.server = server;
    }
    
    public ServerBootStrap filters(Filter... filters) {
        this.filters.addAll(Arrays.asList(filters));
        return this;
    }
    
    @SafeVarargs
    public final <T> void bind(T object, Function<Invoker, Invoker>... function) throws Exception {
        server.bind(object, filters, function);
        server.registerAndListen();
    }
}
