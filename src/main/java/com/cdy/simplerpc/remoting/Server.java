package com.cdy.simplerpc.remoting;

/**
 * 服务端接口
 * Created by 陈东一
 * 2018/11/25 0025 14:41
 */
public interface Server {
    
    void bind(Object... services);
    
    void registerAndListen();
}
