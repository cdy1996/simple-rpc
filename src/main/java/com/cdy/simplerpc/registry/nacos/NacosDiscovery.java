package com.cdy.simplerpc.registry.nacos;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.cdy.simplerpc.exception.DiscoveryException;
import com.cdy.simplerpc.registry.AbstractDiscovery;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * nacos服务发现
 *
 * Created by 陈东一
 * 2019/4/30 0030 18:59
 */
@Slf4j
public class NacosDiscovery extends AbstractDiscovery {
    
    private NamingService namingService;
    private NacosConfig nacosConfig;
    
    public NacosDiscovery(NacosConfig nacosConfig) {
        this.nacosConfig = nacosConfig;
        Properties properties = new Properties();
        properties.setProperty("serverAddr", this.nacosConfig.getServerAddr());
        properties.setProperty("namespace", this.nacosConfig.getNamespaceId());
        
        try {
            namingService = NamingFactory.createNamingService(properties);
        } catch (NacosException e) {
            throw new DiscoveryException("nacos 服务发现监听异常", e);
        }
    }
    
    
    @Override
    public String discovery(String serviceName, String... protocols) throws Exception {
        return loadBalance(serviceName, listServer(serviceName, protocols));
        
    }
    
    @Override
    public List<String> listServer(String serviceName, String... protocols) throws Exception {
        List<Instance> allInstances = namingService.getAllInstances(serviceName, false);
        
        Map<String, List<String>> cache = getCache();
        List<String> cacheList = cache.get(serviceName);
        if (cacheList == null) {
            cacheList = allInstances.stream().map(e -> e.getClusterName() + "-" + e.getIp() + ":" + e.getPort()).collect(Collectors.toList());
            cache.putIfAbsent(serviceName, cacheList);
            subscribe(serviceName);
        }
        return cacheList;
    }
    
    
    public void subscribe(String serviceName, String... protocols) throws NacosException {
        namingService.subscribe(serviceName, event -> {
            log.debug("nacos 服务监听发生变化" + ((NamingEvent) event).getServiceName());
            List<String> collect = ((NamingEvent) event).getInstances().stream().map(e -> e.getClusterName() + "-" + e.getIp() + ":" + e.getPort()).collect(Collectors.toList());
            getCache().get(serviceName).clear();
            //todo 线程安全问题
            getCache().get(serviceName).addAll(collect);
        });
        
    }
}
