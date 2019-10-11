package com.cdy.simplerpc.registry.nacos;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.cdy.simplerpc.config.PropertySources;
import com.cdy.simplerpc.exception.DiscoveryException;
import com.cdy.simplerpc.registry.IServiceRegistry;
import com.cdy.simplerpc.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Properties;

/**
 * nacos实现的注册中心
 *
 * Created by 陈东一
 * 2019/4/30 0030 18:59
 */
@Slf4j
public class NacosRegistry implements IServiceRegistry {
    
    private final NamingService namingService;
    private final PropertySources propertySources;
    
    public NacosRegistry(PropertySources propertySources, String prefix) {
        this.propertySources = propertySources;
        Properties properties = new Properties();
        properties.setProperty("serverAddr", propertySources.resolveProperty(prefix+".serverAddr"));
        properties.setProperty("namespace", propertySources.resolveProperty(prefix+".namespaceId"));
    
        try {
            namingService = NamingFactory.createNamingService(properties);
        } catch (NacosException e) {
            throw new DiscoveryException("nacos 服务注册发生异常", e);
        }
    }
    
    @Override
    public void register(String name, String address) throws Exception {
        String[] split = address.split("-");
        String cluster = split[0];
        address = split[1];
        StringUtil.TwoResult<String, Integer> server = StringUtil.getServer(address);
        log.info("nacos注册 name = {}, address = {}", name, address);
        namingService.registerInstance(name, server.getFirst(), server.getSecond(), cluster);
    }

    @Override
    public void close() {
    }


}
