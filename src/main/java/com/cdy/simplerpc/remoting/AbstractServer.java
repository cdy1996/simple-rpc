package com.cdy.simplerpc.remoting;

import com.cdy.simplerpc.filter.Filter;
import com.cdy.simplerpc.filter.FilterChain;
import com.cdy.simplerpc.proxy.Invoker;
import com.cdy.simplerpc.proxy.ProxyFactory;
import com.cdy.simplerpc.registry.IServiceRegistry;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
    @Getter
    private final Map<String, Invoker> handlerMap = new ConcurrentHashMap<>();
    private final List<IServiceRegistry> registries;
    private final ServerMetaInfo serverMetaInfo;
    
    public AbstractServer(ServerMetaInfo serverMetaInfo, List<IServiceRegistry> registries) {
        this.registries = registries;
        this.serverMetaInfo = serverMetaInfo;
    }
    
    @Override
    public void bind(String serviceName, Object service, List<Filter> filters) throws Exception {
        
        Invoker objectInvoker = ProxyFactory.createWithInstance(service);
        handlerMap.put(serviceName, new FilterChain(objectInvoker, filters));
    }
    
    @Override
    public void register(String serviceName) throws Exception {
        // 注册服务
        for (IServiceRegistry registry : registries) {
            registry.register(serviceName, serverMetaInfo.getProtocol() + "-" + serverMetaInfo.getIp() + ":" + serverMetaInfo.getPort());
        }
    }
    
    
    protected String getAddress() {
        // rpc-127.0.0.1:8080
        return serverMetaInfo.getIp() + ":" + serverMetaInfo.getPort();
    }
    
}
