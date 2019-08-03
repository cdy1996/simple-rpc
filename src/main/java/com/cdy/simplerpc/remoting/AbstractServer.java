package com.cdy.simplerpc.remoting;

import com.cdy.simplerpc.filter.Filter;
import com.cdy.simplerpc.filter.FilterChain;
import com.cdy.simplerpc.proxy.Invoker;
import com.cdy.simplerpc.proxy.ProxyFactory;
import com.cdy.simplerpc.registry.IServiceRegistry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 服务端
 * Created by 陈东一
 * 2018/9/1 21:41
 */
public abstract class AbstractServer implements Server {
    
    /**
     * 存放本地的invoker
     *     key->className
     *     static是因为本地要共享同一个实例
     */
    public static Map<String, Invoker> handlerMap = new HashMap<>();
    private final IServiceRegistry registry;
    private final ServerMetaInfo serverMetaInfo;
    
    public AbstractServer(IServiceRegistry registry, ServerMetaInfo serverMetaInfo) {
        this.registry = registry;
        this.serverMetaInfo = serverMetaInfo;
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
        registry.register(serviceName, serverMetaInfo.getProtocol() + "-" + serverMetaInfo.getAddress() + ":" + serverMetaInfo.getPort());
    }
    
    
    protected String getAddress() {
        // rpc-127.0.0.1:8080
        return serverMetaInfo.getAddress() + ":" + serverMetaInfo.getPort();
    }
    
}
