package com.cdy.simplerpc.registry.nacos;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.cdy.simplerpc.exception.DiscoveryException;
import com.cdy.simplerpc.registry.IServiceRegistry;
import com.cdy.simplerpc.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Properties;

/**
 * nacos实现的注册中心
 * Created by 陈东一
 * 2019/4/30 0030 18:59
 */
@Slf4j
public class NacosRegistry implements IServiceRegistry {
    
    private NamingService namingService;
    private NacosConfig nacosConfig;
    
    public NacosRegistry(NacosConfig nacosConfig) {
        Properties properties = new Properties();
        properties.setProperty("serverAddr", nacosConfig.getServerAddr());
        properties.setProperty("namespace", nacosConfig.getNamespaceId());
    
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
        log.debug("nacos注册 name = {}, address = {}", name, address);
        namingService.registerInstance(name, server.getFirst(), server.getSecond(), cluster);
    }
    
    
   
}
