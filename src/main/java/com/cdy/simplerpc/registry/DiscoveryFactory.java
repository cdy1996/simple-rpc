package com.cdy.simplerpc.registry;

import com.cdy.simplerpc.balance.BalanceFactory;
import com.cdy.simplerpc.config.PropertySources;
import com.cdy.simplerpc.registry.nacos.NacosDiscovery;
import com.cdy.simplerpc.registry.simple.SimpleDiscoveryImpl;
import com.cdy.simplerpc.registry.zookeeper.ZKServiceDiscovery;
import lombok.extern.slf4j.Slf4j;

/**
 * 服务创建工厂
 * <p>
 * Created by 陈东一
 * 2019/5/25 0025 19:51
 */
@Slf4j
public class DiscoveryFactory {
    
    public static IServiceDiscovery createDiscovery(PropertySources propertySources) {
        return createDiscovery(propertySources, null);
    }
    
    public static IServiceDiscovery createDiscovery(PropertySources propertySources, String type) {
        log.info("创建发现中心 {}", type);
        // todo spi
        if ("nacos".equalsIgnoreCase(type)) {
            return new NacosDiscovery(BalanceFactory.createBalance(propertySources), propertySources);
        } else if ("zookeeper".equalsIgnoreCase(type)) {
            return new ZKServiceDiscovery(BalanceFactory.createBalance(propertySources), propertySources);
        } else {
            return new SimpleDiscoveryImpl(BalanceFactory.createBalance(propertySources));
        }
        
    }
}
