package com.cdy.simplerpc.proxy;

import com.cdy.simplerpc.netty.rpc.RPCContext;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class GenericInvocationHandler implements InvocationHandler {

    private final Invoker<?> invoker;
    private final String className;



    public GenericInvocationHandler(Invoker<?> handler, String className) {
        this.invoker = handler;
        this.className = className;
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

        String methodName1  = (String) args[0];

        Arg[] params = (Arg[]) args[2];

        Invocation invocation = new Invocation(methodName1, params, className);

        //方便后续获取对应的元信息
        RPCContext.current().getAttach().put(RPCContext.annotationKey, className);
        return invoker.invoke(invocation);
    }
}
