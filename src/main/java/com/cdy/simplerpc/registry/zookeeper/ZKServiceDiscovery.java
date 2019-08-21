package com.cdy.simplerpc.registry.zookeeper;

import com.cdy.simplerpc.balance.IBalance;
import com.cdy.simplerpc.config.PropertySources;
import com.cdy.simplerpc.exception.DiscoveryException;
import com.cdy.simplerpc.registry.AbstractDiscovery;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 服务发现
 * Created by 陈东一
 * 2018/9/1 21:31
 */
@Slf4j
public class ZKServiceDiscovery extends AbstractDiscovery {
    
    private final PropertySources propertySources;
    private final CuratorFramework curatorFramework;
    private final String registryPath;
    
    public ZKServiceDiscovery(IBalance balance, PropertySources propertySources, String prefix) {
        super(balance);
        this.propertySources = propertySources;
        try {
            registryPath = propertySources.resolveProperty(prefix + "zkRegistryPath");
            curatorFramework = CuratorFrameworkFactory.builder().connectString(propertySources.resolveProperty(prefix+"serverAddr"))
                    .sessionTimeoutMs(4000)
                    .retryPolicy(new ExponentialBackoffRetry(10000, 5))
                    .build();
            curatorFramework.start();
        } catch (Exception e) {
            throw new DiscoveryException("zookeeper 服务发现监听异常", e);
        }
    }

    
    @Override
    public String discovery(String serviceName, String ...protocols) throws Exception {
        //负载均衡
        return loadBalance(serviceName, listServer(serviceName, protocols));
    }
    
    
    @Override
    public List<String> listServer(String serviceName, String ...protocols) throws Exception {
        String path =registryPath + "/" + serviceName;
        // 第一次初始化缓存或者是为空需要重新获取
        Map<String, List<String>> cache = getCache();
        List<String> cacheList = cache.get(serviceName);
        if (cacheList == null) {
            cacheList= curatorFramework.getChildren().forPath(path);
            if (!(protocols == null || protocols.length==0)) {
                cacheList = cacheList.stream().filter(e-> Arrays.stream(protocols).anyMatch(e::startsWith)).collect(Collectors.toList());
            }
            cache.putIfAbsent(serviceName, cacheList);
            subscrible(serviceName);
        }
        return cacheList;
    }
    
    private void subscrible(String serviceName) {
        // 服务监听 更新负载均衡中的可用服务
        try {
            rootListener(serviceName);
        } catch (Exception e) {
            log.error("监听异常", e);
        }
    }
    
    
    private void rootListener(String servicePath) throws Exception{
        PathChildrenCache pathChildrenCache = new PathChildrenCache(curatorFramework, registryPath+"/"+servicePath, true);
        //Normal--初始化为空  BUILD_INITIAL_CACHE--rebuild  POST_INITIALIZED_EVENT--初始化后发送事件
        pathChildrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        pathChildrenCache.getListenable().addListener((curatorFramework1, pathChildrenCacheEvent) -> {
            String path = pathChildrenCacheEvent.getData().getPath();
            String[] split = path.split("/");
            String serviceName = split[0];
            String server = split[1];
            switch (pathChildrenCacheEvent.getType()) {
                case CHILD_ADDED:
                    log.info("增加新的服务节点 service = {}, server = {} ", serviceName, server);
                    getCache().get(serviceName).add(server);
                     break;
                case CHILD_REMOVED:
                    log.info("删除的服务节点 service = {}, server = {} ", serviceName, server);
                    getCache().get(serviceName).remove(server);
                    break;
                case CHILD_UPDATED:
                    // 暂时没有节点更新的操作
                    break;
                default:
                    break;
            }
        });
    }
    
    
}
