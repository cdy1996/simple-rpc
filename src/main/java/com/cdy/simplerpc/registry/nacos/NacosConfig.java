package com.cdy.simplerpc.registry.nacos;

import lombok.Data;

/**
 * zk配置
 * Created by 陈东一
 * 2018/9/1 21:23
 */
@Data
public class NacosConfig {
    
    private String serverAddr = "192.168.72.1:8848";
    private String namespaceId = "529469ac-0341-4276-a256-14dcf863935c";
}
