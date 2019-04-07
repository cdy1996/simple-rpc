package com.cdy.simplerpc.proxy;

import com.cdy.simplerpc.annotation.ReferenceMetaInfo;
import com.cdy.simplerpc.exception.RPCException;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * 服务端本地执行器
 * Created by 陈东一
 * 2019/1/24 0024 23:27
 */
@Slf4j
public class LocalInvoker<T> implements Invoker {
    
    private T t;
    
    public LocalInvoker(T t) {
        this.t = t;
    }
    
    public Object invokeLocal(Invocation invocation) throws Exception {
        Object result;
        Method method = t.getClass().getMethod(invocation.getMethodName(), invocation.getTypes());
        result = method.invoke(t, invocation.getArgs());
        log.debug("执行结果" + result);
        return result;
        
    }
    
    @Override
    public Object invoke(Invocation invocation) throws Exception {
        Object invoke = invokeLocal(invocation);
        if (invoke instanceof Exception) {
            throw new RPCException(((Exception) invoke).getMessage());
        }
        return invoke;
    }
    
    @Override
    public void addMetaInfo(String s, ReferenceMetaInfo data) {
    
    }
}
