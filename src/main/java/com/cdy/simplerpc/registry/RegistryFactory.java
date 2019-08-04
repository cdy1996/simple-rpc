package com.cdy.simplerpc.registry;

import com.cdy.simplerpc.config.PropertySources;
import com.cdy.simplerpc.registry.nacos.NacosRegistry;
import com.cdy.simplerpc.registry.simple.SimpleRegisteryImpl;
import com.cdy.simplerpc.registry.zookeeper.ZKServiceRegistry;

/**
 * 服务创建工厂
 * <p>
 * Created by 陈东一
 * 2019/5/25 0025 19:51
 */
public class RegistryFactory {
    
    
    public static IServiceRegistry createRegistry(PropertySources propertySources) {
        
        return createRegistry(propertySources, null);
    }
    
    public static IServiceRegistry createRegistry(PropertySources propertySources, String type) {
        // todo spi
        
        if (type.startsWith("nacos")) {
            return new NacosRegistry(propertySources,  type.replace("nacos-",""));
        } else if (type.startsWith("zookeeper")) {
            return new ZKServiceRegistry(propertySources, type.replace("zookeeper-",""));
        } else {
            return new SimpleRegisteryImpl();
        }
        
    }
}
