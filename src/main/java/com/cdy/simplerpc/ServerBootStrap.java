package com.cdy.simplerpc;

import com.cdy.simplerpc.filter.Filter;
import com.cdy.simplerpc.proxy.Invoker;
import com.cdy.simplerpc.registry.IServiceRegistry;
import com.cdy.simplerpc.registry.simple.SimpleRegisteryImpl;
import com.cdy.simplerpc.remoting.Server;
import com.cdy.simplerpc.remoting.netty.RPCServer;

import java.util.ArrayList;
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
    
    public ServerBootStrap registry(IServiceRegistry registry) {
        this.registry = registry;
        return this;
    }
    
    public ServerBootStrap server(Server server) {
        this.server = server;
        assert registry != null;
        server.setRegistry(registry);
        return this;
    }
    
    public ServerBootStrap filters(Filter... filters) {
        for (Filter filter : filters) {
            this.filters.add(filter);
        }
        return this;
    }
    
    
    public void bind(Object object, Function<Invoker, Invoker>... function) throws Exception {
        server.bind(object, filters, function);
        server.registerAndListen();
    }
}
