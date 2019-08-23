package com.cdy.simplerpc.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * 代理工厂
 * Created by 陈东一
 * 2018/11/25 0025 14:27
 */
public class ProxyFactory {
    
    //创建代理对象
    public static <T>T createProxy(Invoker invoker, Class<T> clazz) {
        return createProxy(clazz, new InvokerInvocationHandler(invoker, clazz));
        
    }

    public static <T>T createProxy(Class<T> clazz, InvocationHandler invokerInvocationHandler) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(),
                new Class<?>[]{clazz}, invokerInvocationHandler);
    }
    
    // 创建invoker
    public static <T> Invoker<T> createWithInstance(T t){
        Invoker<T> invoker = new LocalInvoker<>(t);
        return invoker;
    }
}
