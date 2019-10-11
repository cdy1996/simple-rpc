package com.cdy.simplerpc.registry;

/**
 * 服务注册接口
 * Created by 陈东一
 * 2018/9/1 21:18
 */
public interface IServiceRegistry {
    
    /**
     * serviceName 与 SerivceAddress绑定
     * @param name
     * @param address
     */
    void register(String name, String address) throws Exception;

    void close();
}
