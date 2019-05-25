package com.cdy.simplerpc.remoting;

import com.cdy.simplerpc.filter.Filter;
import com.cdy.simplerpc.filter.FilterChain;
import com.cdy.simplerpc.proxy.Invoker;
import com.cdy.simplerpc.proxy.ProxyFactory;
import com.cdy.simplerpc.registry.IServiceRegistry;

import java.io.Closeable;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

/**
 * 服务端
 * Created by 陈东一
 * 2018/9/1 21:41
 */
public abstract class AbstractServer implements Server, Closeable {
    
    //服务名称, 执行器   同一个server可以用多个执行器
    private HashMap<String, Invoker> handlerMap = new HashMap<>();
    private IServiceRegistry registry;
    private String protocol;
    private String port;
    private String address;
    
    public AbstractServer(String protocol, String port, String address) {
        this.protocol = protocol;
        this.port = port;
        this.address = address;
    }
    
    @Override
    public void bind(String serviceName, Object service, List<Filter> filters, Function<Invoker, Invoker>... functions) throws Exception {
        
        Invoker objectInvoker = ProxyFactory.createWithInstance(service);
        for (Function<Invoker, Invoker> function : functions) {
            objectInvoker = function.apply(objectInvoker);
        }
        
        handlerMap.put(serviceName, new FilterChain(objectInvoker, filters));
    }
    
    @Override
    public void register(String serviceName) throws Exception {
        // 注册服务
        registry.register(serviceName, protocol + "-" + address + ":" + port);
    }
    
    public IServiceRegistry getRegistry() {
        return registry;
    }
    
    @Override
    public void setRegistry(IServiceRegistry registry) {
        this.registry = registry;
    }
    
    public String getAddress() {
        // rpc-127.0.0.1:8080
        return address + ":" + port;
    }
    
    public HashMap<String, Invoker> getHandlerMap() {
        return handlerMap;
    }
}
