package com.cdy.simplerpc.proxy;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 执行处理
 *
 * Created by 陈东一
 * 2019/1/26 0026 20:45
 */
public class InvokerInvocationHandler implements InvocationHandler {
    
    private final Invoker<?> invoker;
    private Class<?> clazz;
    
    public InvokerInvocationHandler(Invoker<?> handler, Class<?> clazz) {
        this.invoker = handler;
        this.clazz = clazz;
    }
    
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(invoker, args);
        }
        if ("toString".equals(methodName) && parameterTypes.length == 0) {
            return invoker.toString();
        }
        if ("hashCode".equals(methodName) && parameterTypes.length == 0) {
            return invoker.hashCode();
        }
        if ("equals".equals(methodName) && parameterTypes.length == 1) {
            return invoker.equals(args[0]);
        }
        return invoker.invoke(new Invocation(method, args, clazz));
    }
    
}