package com.cdy.simplerpc.proxy;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import static com.cdy.simplerpc.annotation.ReferenceMetaInfo.METAINFO_KEY;

/**
 * 执行处理
 *
 * Created by 陈东一
 * 2019/1/26 0026 20:45
 */
public class InvokerInvocationHandler implements InvocationHandler {
    
    private final Invoker<?> invoker;
    private Class<?> clazz;
    private String key; //元信息所属的key
    
    public InvokerInvocationHandler(Invoker<?> handler, Class<?> clazz, String key) {
        this.invoker = handler;
        this.clazz = clazz;
        this.key = key;
    }
    
    public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
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
        Invocation invocation = new Invocation(method, args, clazz);
        
        //方便后续获取对应的元信息
        invocation.getAttach().put(METAINFO_KEY, key);
        return invoker.invoke(invocation);
    }
    
}