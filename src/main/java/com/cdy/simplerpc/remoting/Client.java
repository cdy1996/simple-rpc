package com.cdy.simplerpc.remoting;

import com.cdy.simplerpc.proxy.Invocation;
import io.netty.channel.Channel;

/**
 * 客户端接口
 * Created by 陈东一
 * 2018/11/25 0025 14:41
 */
public interface Client {
    
    void init();
    
    Object invoke(Invocation invocation) throws Exception;
    
    Channel connect(String serviceName);
    
    void close();
}
