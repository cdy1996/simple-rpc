package com.cdy.simplerpc;

import com.cdy.simplerpc.config.DiscoveryConfig;
import com.cdy.simplerpc.config.RemotingConfig;
import com.cdy.simplerpc.container.RPCReference;
import com.cdy.simplerpc.proxy.Invoker;
import com.cdy.simplerpc.proxy.ProxyFactory;
import com.cdy.simplerpc.proxy.RemoteInvoker;
import com.cdy.simplerpc.registry.IServiceDiscovery;
import com.cdy.simplerpc.registry.simple.SimpleDiscoveryImpl;
import com.cdy.simplerpc.remoting.Client;
import com.cdy.simplerpc.remoting.netty.RPCClient;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * todo
 * Created by 陈东一
 * 2019/1/22 0022 22:11
 */
public class ClientBootStrap {
    
    private IServiceDiscovery discovery;
    
    private Client client;
    
    private ProxyFactory proxyFactory;
    
    private Map<String, Invoker> invokerMap = new ConcurrentHashMap<>();
    
    
    public ClientBootStrap start(DiscoveryConfig discoveryConfig, RemotingConfig remotingConfig) {
    
        IServiceDiscovery discovery = new SimpleDiscoveryImpl();
        this.discovery = discovery;
        
        Client client = new RPCClient(discovery);
        this.client = client;
    
        this.proxyFactory = new ProxyFactory();
        
        return this;
    }
    
    public Object inject(Object t, Function<Invoker, Invoker> ... function){
        Field[] fields = t.getClass().getDeclaredFields();
        for (Field field : fields) {
            RPCReference annotation = field.getAnnotation(RPCReference.class);
            if (annotation != null) {
                Class<?> clazz = field.getType();
                synchronized (clazz.getName()) {
                    Invoker invoker = invokerMap.get(clazz.getName());
                    field.setAccessible(true);
                    try {
                        if (invoker != null) {
                            field.set(t, invoker);
                            continue;
                        }
                        invoker = new RemoteInvoker(client);
                        for (Function<Invoker, Invoker> invokerInvokerFunction : function) {
                            invoker = invokerInvokerFunction.apply(invoker);
                        }
                        invokerMap.put(clazz.getName(), invoker);
                        Object proxy = proxyFactory.createProxy(invoker, clazz);
                        field.set(t, proxy);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return t;
    }
}
