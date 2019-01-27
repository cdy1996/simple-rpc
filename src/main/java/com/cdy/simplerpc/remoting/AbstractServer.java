package com.cdy.simplerpc.remoting;

import com.cdy.simplerpc.container.RPCService;
import com.cdy.simplerpc.filter.FilterInvokerWrapper;
import com.cdy.simplerpc.proxy.Invoker;
import com.cdy.simplerpc.proxy.ProxyFactory;
import com.cdy.simplerpc.registry.IServiceRegistry;

import java.util.HashMap;
import java.util.function.Function;

/**
 * 服务端
 * Created by 陈东一
 * 2018/9/1 21:41
 */
public abstract class AbstractServer implements Server {
    
    //服务类和方法列表
    public static HashMap<String, Invoker> handlerMap = new HashMap<>();
    private IServiceRegistry registry;
    private String address;
    
    public AbstractServer(IServiceRegistry registry, String address) {
        this.registry = registry;
        this.address = address;
    }
    
    @Override
    public void bind(Object service, Function<Invoker, Invoker>... functions) {
        RPCService annotation = service.getClass().getAnnotation(RPCService.class);
        String serviceName = annotation.clazz().getName();
        Invoker objectInvoker = ProxyFactory.createWithInstance(service);
        for (Function<Invoker, Invoker> function : functions) {
            objectInvoker = function.apply(objectInvoker);
        }
        
        handlerMap.put(serviceName, new FilterInvokerWrapper(objectInvoker));
    }
    
    public void register(){
        // 注册服务
        for (String s : handlerMap.keySet()) {
            //注册服务和地址
            registry.register(s, address);
        }
    }
    
    public IServiceRegistry getRegistry() {
        return registry;
    }
    
    public void setRegistry(IServiceRegistry registry) {
        this.registry = registry;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
}