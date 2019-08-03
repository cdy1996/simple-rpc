package com.cdy.simplerpc.registry;

import java.util.List;

/**
 * 服务搭建接口
 * Created by 陈东一
 * 2018/9/1 21:31
 */
public interface IServiceDiscovery {
    
    /**
     * 发现服务
     *
     * @param serviceName
     * @param protocol
     * @return rpc-127.0.0.1:8080
     * @throws Exception
     */
    String discovery(String serviceName, String ...protocol) throws Exception;
    
    /**
     * 服务对应的所有实例地址
     *
     * @param serviceName
     * @return rpc-127.0.0.1:8080  http-127.0.0.1:8082
     * @throws Exception
     */
    List<String> listServer(String serviceName,  String ...protocols) throws Exception;
    

    
}
