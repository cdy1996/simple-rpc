package com.cdy.simplerpc.registry.nacos;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.cdy.simplerpc.balance.IBalance;
import com.cdy.simplerpc.config.PropertySources;
import com.cdy.simplerpc.exception.DiscoveryException;
import com.cdy.simplerpc.registry.AbstractDiscovery;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
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
    
    private final NamingService namingService;
    private final PropertySources propertySources;
    
    public NacosDiscovery(IBalance balance, PropertySources propertySources, String prefix) {
        super(balance);
        this.propertySources = propertySources;
        Properties properties = new Properties();
        properties.setProperty("serverAddr", propertySources.resolveProperty(prefix+"serverAddr"));
        properties.setProperty("namespace",  propertySources.resolveProperty(prefix+"namespace"));
    
        try {
            namingService = NamingFactory.createNamingService(properties);
        } catch (NacosException e) {
            throw new DiscoveryException("nacos 服务发现监听异常", e);
        }
    }
    
    
    @Override
    public String discovery(String serviceName, String... protocols) throws Exception {
        return loadBalance(serviceName, getCache().get(serviceName)); //只读取缓存
        
    }
    
    @Override
    public List<String> listServer(String serviceName, String... protocols) throws Exception {
        Map<String, List<String>> cache = getCache();
        List<String> cacheList = cache.get(serviceName);
        if (cacheList == null) { //启动时需要加载服务列表到缓存
            List<Instance> allInstances = namingService.getAllInstances(serviceName, false);
            cacheList = allInstances.stream().map(e -> e.getClusterName() + "-" + e.getIp() + ":" + e.getPort())
                    .collect(Collectors.toList());
            if (!(protocols == null || protocols.length==0)) { //过滤需要的协议
                cacheList = cacheList.stream()
                        .filter(e-> Arrays.stream(protocols).anyMatch(e::startsWith))
                        .collect(Collectors.toList());
            }
            cache.putIfAbsent(serviceName, cacheList);
            subscribe(serviceName, protocols);
        }
        return cacheList;
    }

    @Override
    public void close() {
    }


    private void subscribe(String serviceName, String... protocols) throws NacosException {
        namingService.subscribe(serviceName, event -> {
            log.info("nacos 服务监听发生变化 {}" ,((NamingEvent) event).getServiceName());
            List<String> collect = ((NamingEvent) event).getInstances().stream().map(e -> e.getClusterName() + "-" + e.getIp() + ":" + e.getPort()).collect(Collectors.toList());
            getCache().get(serviceName).clear();
            if (!(protocols == null || protocols.length==0)) {
                collect = collect.stream()
                        .filter(e-> Arrays.stream(protocols).anyMatch(e::startsWith))
                        .collect(Collectors.toList());
            }
            //todo 线程安全问题
            getCache().get(serviceName).addAll(collect);
        });
        
    }
}
