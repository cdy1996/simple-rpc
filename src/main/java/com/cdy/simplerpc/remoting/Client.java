package com.cdy.simplerpc.remoting;

import com.cdy.simplerpc.proxy.Invocation;

/**
 * 客户端接口
 * Created by 陈东一
 * 2018/11/25 0025 14:41
 */
public interface Client {

    
    /**
     * 远程调用
     * @param invocation
     */
    Object invoke(Invocation invocation) throws Exception;
    
    /**
     * 关闭
     */
    void close();
 

}
