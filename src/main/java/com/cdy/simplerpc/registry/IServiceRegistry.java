package com.cdy.simplerpc.registry;

/**
 * todo 描述
 * Created by 陈东一
 * 2018/9/1 21:18
 */
public interface IServiceRegistry {
    
    //serviceName 与 SerivceAddress绑定
    
    void register(String name, String address);
}
