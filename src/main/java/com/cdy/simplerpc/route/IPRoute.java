package com.cdy.simplerpc.route;

import com.cdy.simplerpc.config.PropertySources;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 路由
 * Created by 陈东一
 * 2019/5/26 0026 10:34
 */
public class IPRoute implements IRoute{
    
    PropertySources propertySources;
    String address;
    
    /**
     * 筛选服务地址
     * @param list  服务实例列表
     */
    @Override
    public List<String> route(List<String> list){
        //从配置中心拉取有用的路由信息
        //自己的ip -> 可访问的ip
        List<String> ips = null;
        
        // 服务端的ip
        // rpc-127.0.0.1:8080 -> 127.0.0.1
        // rpc-127.0.0.1:8081 -> 127.0.0.1
        // http-127.0.0.1:80 -> 127.0.0.1
        
        return list.stream().filter(ips::contains).collect(Collectors.toList());
        
    
    }
}
