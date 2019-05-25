package com.cdy.simplerpc.registry.zookeeper;

import com.cdy.simplerpc.config.PropertySources;
import com.cdy.simplerpc.registry.IServiceRegistry;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * 服务注册
 * Created by 陈东一
 * 2018/9/1 21:19
 */
@Slf4j
public class ZKServiceRegistry implements IServiceRegistry {
    
    private CuratorFramework curatorFramework;
    private PropertySources propertySources;
    
    
    public ZKServiceRegistry(PropertySources propertySources) {
        this.propertySources = propertySources;
        curatorFramework = CuratorFrameworkFactory.builder().connectString(propertySources.resolveProperty("zkAddress"))
                .sessionTimeoutMs(4000)
                .retryPolicy(new ExponentialBackoffRetry(10000, 5))
                .build();
        curatorFramework.start();
    }
    
    @Override
    public void register(String name, String address) throws Exception {
        String servicePath = propertySources.resolveProperty("zkRegistryPath") + "/" + name;
        
        if (curatorFramework.checkExists().forPath(servicePath) == null) {
            curatorFramework.create().creatingParentContainersIfNeeded()
                    .withMode(CreateMode.PERSISTENT)
                    .forPath(servicePath, "0".getBytes());
        }
        log.info("serviceName 路径创建成功" + servicePath);
        
        String addressPath = servicePath + "/" + address;
        String addNode = curatorFramework.create().withMode(CreateMode.EPHEMERAL)
                .forPath(addressPath, "0".getBytes());
        log.info("address 路径创建成功 " + addNode);
        
    }
}
