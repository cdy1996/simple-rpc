package com.cdy.simplerpc.registry.zookeeper;

import lombok.Data;

/**
 * zk配置
 * Created by 陈东一
 * 2018/9/1 21:23
 */
@Data
public class ZKConfig {
    
    private String zkAddress = "127.0.0.1:2181";
    private String zkRegistryPath = "/registry";
}
