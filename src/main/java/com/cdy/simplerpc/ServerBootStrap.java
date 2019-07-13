package com.cdy.simplerpc;

import com.cdy.simplerpc.annotation.RPCService;
import com.cdy.simplerpc.filter.Filter;
import com.cdy.simplerpc.proxy.Invoker;
import com.cdy.simplerpc.registry.IServiceRegistry;
import com.cdy.simplerpc.remoting.Server;
import com.cdy.simplerpc.remoting.ServerFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;
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
    
    private List<Filter> filters = new ArrayList<>();
    
    private Map<String, Server> servers = new HashMap<>();
    
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
        executor.execute(() -> {
            filters.addAll(this.filters);
            try {
                RPCService annotation = object.getClass().getAnnotation(RPCService.class);
                if (annotation == null) {
                    return;
                }
                Server server;
                //因为相同的协议和端口就不用不多起服务了,会占用端口号
                if ((server = servers.get(protocol + port)) == null) {
                    server = ServerFactory.createServer(protocol, port);
                    server.setRegistry(registry);
                    servers.put(protocol + port, server);
    
                    String serviceName = object.getClass().getName();
                    server.bind(serviceName, object, filters, function);
                    server.openServer();
                    server.register(serviceName);
                } else {
                    //不用重复开启服务
                    String serviceName = object.getClass().getName();
                    server.bind(serviceName, object, filters, function);
                    server.register(serviceName);
                }
           
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        });
        
        
    }
    
    public void closeAll() {
        servers.forEach((k, v) -> {
                    if (v instanceof Closeable) {
                        try {
                            ((Closeable) v).close();
                        } catch (IOException e) {
                            log.error(e.getMessage(), e);
                        }
                        
                    }
                }
        );
    }
}
