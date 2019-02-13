package com.cdy.simplerpc.registry.zookeeper;

import com.cdy.simplerpc.registry.AbstractDiscovery;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 服务发现
 * Created by 陈东一
 * 2018/9/1 21:31
 */
public class ZKServiceDiscoveryImpl extends AbstractDiscovery {
    
    CuratorFramework curatorFramework;
    List<String> list = new ArrayList<>();
    
    
    public ZKServiceDiscoveryImpl() {
        init();
        try {
            listener();
        } catch (Exception e) {
            throw new RuntimeException("zookeeper 服务发现监听异常", e);
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
        list = curatorFramework.getChildren().forPath(path);
        //负载均衡
        return loadBalance(serviceName, list);
    }
    
    
    public void listener() throws Exception {
        // 服务监听 更新负载均衡中的可用服务
        rootListener();
    }
    
    public void rootListener() throws Exception{
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
                    System.out.println("增加新的服务节点");
                    getBalance().addServer(serviceName, Collections.singletonList(server));
                    break;
                case CHILD_REMOVED:
                    System.out.println("删除服务节点");
                    pathChildrenCacheEvent.getType();
                    getBalance().deleteServer(serviceName, null);
                    break;
                case CHILD_UPDATED:
                    getBalance().deleteServer(serviceName, null);
                    getBalance().addServer(serviceName, Collections.singletonList(server));
                    break;
                default:
                    break;
            }
        });
    }
    
    
}
