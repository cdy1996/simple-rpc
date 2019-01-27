package com.cdy.simplerpc.proxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 服务端本地执行器
 * Created by 陈东一
 * 2019/1/24 0024 23:27
 */
public class LocalInvoker<T> implements Invoker {
    
    private T t;
    
    public LocalInvoker(T t) {
        this.t = t;
    }
    
    public Object invokeLocal(Invocation invocation) throws Exception {
        Object result = null;
        try {
            Method method = t.getClass().getMethod(invocation.getMethodName(), invocation.getTypes());
            result = method.invoke(t, invocation.getArgs());
            System.out.println("执行结果" + result);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return result;
        
    }
    
    @Override
    public Object invoke(Invocation invocation) throws Exception {
        return invokeLocal(invocation);
    }
}