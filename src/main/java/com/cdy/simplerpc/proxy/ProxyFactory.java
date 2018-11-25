package com.cdy.simplerpc.proxy;

import com.cdy.simplerpc.remoting.Client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 代理工厂
 * Created by 陈东一
 * 2018/11/25 0025 14:27
 */
public class ProxyFactory {
    
    Client client;
    
    public ProxyFactory(Client client) {
        this.client = client;
    }
    
    public <T>T create(Class<T> interfaceClass) {
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                       return client.invoke(method, args, interfaceClass);
                    }
                });
        
    }
}
