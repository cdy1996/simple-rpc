package com.cdy.simplerpc;

import com.cdy.simplerpc.filter.Filter;
import com.cdy.simplerpc.proxy.Invoker;
import com.cdy.simplerpc.registry.IServiceRegistry;
import com.cdy.simplerpc.registry.simple.SimpleRegisteryImpl;
import com.cdy.simplerpc.remoting.Server;
import com.cdy.simplerpc.remoting.jetty.HttpServer;
import com.cdy.simplerpc.remoting.netty.RPCServer;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

/**
 * 服务端启动类
 * Created by 陈东一
 * 2019/1/22 0022 22:11
 */
@Slf4j
public class ServerBootStrap {
    
    private IServiceRegistry registry = new SimpleRegisteryImpl();
    
    private Server server = new RPCServer("127.0.0.1:8899");
    
    private List<Filter> filters = new ArrayList<>();
    
    private Set<Server> servers = new HashSet<>();
    
    private ExecutorService executor = Executors.newCachedThreadPool();
    
   //通用filter
    public ServerBootStrap filters(Filter... filters) {
        this.filters.addAll(Arrays.asList(filters));
        return this;
    }
    
    @SafeVarargs
    public final <T> void bind(Server server, List<Filter> filters, T object, Function<Invoker, Invoker>... function) throws Exception {
        servers.add(server);
        executor.execute(() -> {
            filters.addAll(this.filters);
            server.bind(object, filters, function);
            try {
                server.registerAndListen();
            } catch (Exception e) {
                log.error(e.getMessage(),e);
            }
        });
    
    }
    
    public void closeAll(){
        RPCServer.servers.forEach((k,v)->v.close());
        HttpServer.servers.forEach((k, v)->v.close());
    }
}
