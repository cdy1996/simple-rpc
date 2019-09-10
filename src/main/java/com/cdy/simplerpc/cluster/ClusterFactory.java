package com.cdy.simplerpc.cluster;

import com.cdy.simplerpc.config.ConfigConstants;
import com.cdy.simplerpc.config.PropertySources;
import com.cdy.simplerpc.registry.IServiceDiscovery;
import com.cdy.simplerpc.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 集群工厂
 * Created by 陈东一
 * 2019/9/8 0008 16:00
 */
@Slf4j
public class ClusterFactory {

    public static ClusterClient createCluster(PropertySources propertySources, Map<String, IServiceDiscovery> servceDiscoveryMap, String type) {
        // todo spi
        String defaultCluster = propertySources.resolveProperty("discovery.custer");
        String cluster = propertySources.resolveProperty(type + "." + ConfigConstants.cluster);
        cluster = StringUtil.isBlank(cluster) ? defaultCluster : cluster;
        log.info("选择的集群策略为 -> {}", cluster);
        if (cluster.equalsIgnoreCase("failcache")) {
            return new FailCacheClusterClient(propertySources, servceDiscoveryMap);
        } else if (cluster.startsWith("failover")) {
            return new FailOverClusterClient(propertySources, servceDiscoveryMap);
        } else {
            return new FailFastClusterClient(propertySources, servceDiscoveryMap);
        }

    }
}
