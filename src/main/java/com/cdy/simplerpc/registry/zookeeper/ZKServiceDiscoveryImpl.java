package com.cdy.simplerpc.registry.zookeeper;

import com.cdy.simplerpc.balance.IBalance;
import com.cdy.simplerpc.registry.AbstractDiscovery;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务发现
 * Created by 陈东一
 * 2018/9/1 21:31
 */
public class ZKServiceDiscoveryImpl extends AbstractDiscovery {
    
    CuratorFramework curatorFramework;
    List<String> list = new ArrayList<>();
    
    
    public ZKServiceDiscoveryImpl(IBalance balance) {
        super(balance);
        init();
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
    }
    
    public void rootListener() throws Exception{
        PathChildrenCache pathChildrenCache = new PathChildrenCache(curatorFramework, ZKConfig.zkRegistryPath, true);
        //Normal--初始化为空  BUILD_INITIAL_CACHE--rebuild  POST_INITIALIZED_EVENT--初始化后发送事件
        pathChildrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        pathChildrenCache.getListenable().addListener((curatorFramework1, pathChildrenCacheEvent) -> {
            switch (pathChildrenCacheEvent.getType()) {
                case CHILD_ADDED:
                    System.out.println("增加新的服务节点");
                    //需要对新的路径进行 新的监听
                    childListener(pathChildrenCacheEvent.getData().getPath());
                    break;
                case CHILD_REMOVED:
                    System.out.println("删除服务节点");
                    //某个服务挂了
                    break;
                case CHILD_UPDATED:
                    //根节点不关心该事件
                    break;
                default:
                    break;
            }
        });
    }
    
    public void childListener(String childPath) throws Exception{
        PathChildrenCache pathChildrenCache = new PathChildrenCache(curatorFramework, childPath, true);
        //Normal--初始化为空  BUILD_INITIAL_CACHE--rebuild  POST_INITIALIZED_EVENT--初始化后发送事件
        pathChildrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        pathChildrenCache.getListenable().addListener((curatorFramework1, pathChildrenCacheEvent) -> {
            switch (pathChildrenCacheEvent.getType()) {
                case CHILD_ADDED:
                    System.out.println("增加节点");
                    // 需要更新负载均衡中的机器数
                    break;
                case CHILD_REMOVED:
                    System.out.println("删除节点");
                    // 需要更新负载均衡中的机器数
                    break;
                case CHILD_UPDATED:
                    System.out.println("更新节点");
                    // 现在没用,以后如果有其他信息附加的话需要更新(例如 超时等熟悉)
                    break;
                default:
                    break;
            }
        });
    }
}
