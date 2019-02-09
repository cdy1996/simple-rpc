package com.cdy.simplerpc.registry;

/**
 * 服务搭建接口
 * Created by 陈东一
 * 2018/9/1 21:31
 */
public interface IServiceDiscovery {
    
    /**
     * 发现服务
     * @param serviceName
     * @return
     * @throws Exception
     */
    String discovery(String serviceName) throws Exception;
}
