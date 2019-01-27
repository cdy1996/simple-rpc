package com.cdy.simplerpc.remoting;

import com.cdy.simplerpc.proxy.Invocation;

/**
 * 客户端接口
 * Created by 陈东一
 * 2018/11/25 0025 14:41
 */
public interface Client {
    
    void init();
    
    Object invoke(Invocation invocation) throws Exception;
    
    void close();
}
