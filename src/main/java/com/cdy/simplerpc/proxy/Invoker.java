package com.cdy.simplerpc.proxy;

/**
 * 执行器
 * Created by 陈东一
 * 2019/1/24 0024 23:27
 */
public interface Invoker<T> {
    
    Object invoke(Invocation invocation) throws Exception;
}
