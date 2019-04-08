package com.cdy.simplerpc.registry.zookeeper;

import com.cdy.simplerpc.exception.DiscoveryException;
import com.cdy.simplerpc.registry.AbstractDiscovery;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.List;
import java.util.Map;

/**
 * 服务发现
 * Created by 陈东一
 * 2018/9/1 21:31
 */
@Slf4j
public class ZKServiceDiscoveryImpl extends AbstractDiscovery {
    
    CuratorFramework curatorFramework;
    
    
    public ZKServiceDiscoveryImpl() {
        init();
        try {
            listener();
        } catch (Exception e) {
            throw new DiscoveryException("zookeeper 服务发现监听异常", e);
        }
    }
    
    public void init() {
        curatorFramework = CuratorFrameworkFactory.builder().connectString(ZKConfig.zkAddress)
                .sessionTimeoutMs(4000)
                .retryPolicy(new ExponentialBackoffRetry(10000, 5))
                .build();
        curatorFramework.start();
    }
    
    @Override
    public String discovery(String serviceName) throws Exception {
        String path = ZKConfig.zkRegistryPath + "/" + serviceName;
        //发现地址
        Map<String, List<String>> cache = getCache();
        List<String> list = null;
        if ((list = cache.get(serviceName)) == null) {
            list = curatorFramework.getChildren().forPath(path);
            cache.put(serviceName, list);
        }
        //负载均衡
        return loadBalance(serviceName, list);
    }
    
    
    @Override
    public List<String> listServer(String serviceName) {
        String path = ZKConfig.zkRegistryPath + "/" + serviceName;
        //发现地址
        Map<String, List<String>> cache = getCache();
        List<String> servers = null;
        if ((servers = cache.get(serviceName)) == null) {
            try {
                servers = curatorFramework.getChildren().forPath(path);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                //如果创建失败可能是被其他线程先创建了,所以尝试直接返回
                return cache.get(serviceName);
            }
            cache.putIfAbsent(serviceName, servers);
        }
        return servers;
    }
    
    
    public void listener() throws Exception {
        // 服务监听 更新负载均衡中的可用服务
        rootListener();
    }
    
    
    public void rootListener() throws Exception {
        PathChildrenCache pathChildrenCache = new PathChildrenCache(curatorFramework, ZKConfig.zkRegistryPath, true);
        //Normal--初始化为空  BUILD_INITIAL_CACHE--rebuild  POST_INITIALIZED_EVENT--初始化后发送事件
        pathChildrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        pathChildrenCache.getListenable().addListener((curatorFramework1, pathChildrenCacheEvent) -> {
            String path = pathChildrenCacheEvent.getData().getPath();
            String[] split = path.split("/");
            String serviceName = split[0];
            String server = split[1];
            switch (pathChildrenCacheEvent.getType()) {
                case CHILD_ADDED:
                    log.debug("增加新的服务节点");
                    getCache().remove(serviceName);
                    //todo 更新这里的cache即可 ,不用更新ribbon中的list
//                    getBalance().addServer(serviceName, Collections.singletonList(server));
                    break;
                case CHILD_REMOVED:
                    log.debug("删除服务节点");
                    getCache().remove(serviceName);
//                    pathChildrenCacheEvent.getType();
//                    getBalance().deleteServer(serviceName, null);
                    break;
                case CHILD_UPDATED:
                    // 暂时没有节点更新的操作
                    /*cache.remove(serviceName);
                    getBalance().deleteServer(serviceName, null);
                    getBalance().addServer(serviceName, Collections.singletonList(server));*/
                    break;
                default:
                    break;
            }
        });
    }
    
    
}
