package com.cdy.simplerpc;

import com.cdy.simplerpc.config.DiscoveryConfig;
import com.cdy.simplerpc.config.RemotingConfig;
import com.cdy.simplerpc.container.RPCReference;
import com.cdy.simplerpc.proxy.ProxyFactory;
import com.cdy.simplerpc.registry.IServiceDiscovery;
import com.cdy.simplerpc.registry.simple.SimpleDiscoveryImpl;
import com.cdy.simplerpc.remoting.Client;
import com.cdy.simplerpc.remoting.netty.RPCClient;

import java.lang.reflect.Field;

/**
 * todo
 * Created by 陈东一
 * 2019/1/22 0022 22:11
 */
public class ClientBootStrap {
    
    private IServiceDiscovery discovery;
    
    private Client client;
    
    private ProxyFactory proxyFactory;
    
    
    public ClientBootStrap start(DiscoveryConfig discoveryConfig, RemotingConfig remotingConfig) {
    
        IServiceDiscovery discovery = new SimpleDiscoveryImpl();
        this.discovery = discovery;
        
        Client client = new RPCClient(discovery);
        this.client = client;
    
        this.proxyFactory = new ProxyFactory(client);
        
        return this;
    }
    
    public <T>T inject(T t){
        Field[] fields = t.getClass().getFields();
        for (Field field : fields) {
            RPCReference annotation = field.getAnnotation(RPCReference.class);
            if (annotation != null) {
                Class<?> clazz = t.getClass();
                field.setAccessible(true);
                try {
                    field.set(t, proxyFactory.create(clazz));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return t;
    }
}
