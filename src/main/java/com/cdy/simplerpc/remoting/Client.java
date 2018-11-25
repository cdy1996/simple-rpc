package com.cdy.simplerpc.remoting;

import java.lang.reflect.Method;

/**
 * 客户端接口
 * Created by 陈东一
 * 2018/11/25 0025 14:41
 */
public interface Client {
    
    
    <T> Object invoke(Method method, Object[] args, Class<T> interfaceClass);
}
