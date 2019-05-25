package com.cdy.simplerpc;

import com.cdy.simplerpc.annotation.RPCService;
import com.cdy.simplerpc.filter.Filter;
import com.cdy.simplerpc.proxy.Invoker;
import com.cdy.simplerpc.registry.IServiceRegistry;
import com.cdy.simplerpc.remoting.Server;
import com.cdy.simplerpc.remoting.ServerFactory;
import com.cdy.simplerpc.remoting.http.HttpServer;
import com.cdy.simplerpc.remoting.rpc.RPCServer;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
    
    private List<Filter> filters = new ArrayList<>();
    
    private HashMap<String, Server> servers = new HashMap<>();
    
    //todo 以后增加多注册中心
    private IServiceRegistry registry;
    
    private ExecutorService executor = Executors.newCachedThreadPool();
    
   //通用filter
    public ServerBootStrap filters(Filter... filters) {
        this.filters.addAll(Arrays.asList(filters));
        return this;
    }
    
    public void setRegistry(IServiceRegistry registry) {
        this.registry = registry;
    }
    
    @SafeVarargs
    public final <T> void bind(String protocol, String port, List<Filter> filters, T object, Function<Invoker, Invoker>... function) throws Exception {
        Server server;
        if ((server =  servers.get(protocol + port)) == null) {
            server = ServerFactory.createServer(protocol, port);
            server.setRegistry(registry);
            servers.put(protocol + port, server);
        }
        Server finalServer = server;
        executor.execute(() -> {
            filters.addAll(this.filters);
            try {
                RPCService annotation = object.getClass().getAnnotation(RPCService.class);
                String serviceName = annotation.clazz().getName();
                finalServer.bind(serviceName, object, filters, function);
                finalServer.openServer();
                finalServer.register(serviceName);
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
