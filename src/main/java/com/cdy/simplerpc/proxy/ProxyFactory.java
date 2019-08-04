package com.cdy.simplerpc.proxy;

import java.lang.reflect.Proxy;

/**
 * 代理工厂
 * Created by 陈东一
 * 2018/11/25 0025 14:27
 */
public class ProxyFactory {
    
    //创建代理对象
    public static Object createProxy(Invoker invoker, Class clazz) {
        return  Proxy.newProxyInstance(invoker.getClass().getClassLoader(),
                new Class<?>[]{clazz}, new InvokerInvocationHandler(invoker, clazz));
        
    }
    
    // 创建invoker
    public static <T> Invoker createWithInstance(T t){
        Invoker invoker = new LocalInvoker<T>(t);
        return invoker;
    }
}
