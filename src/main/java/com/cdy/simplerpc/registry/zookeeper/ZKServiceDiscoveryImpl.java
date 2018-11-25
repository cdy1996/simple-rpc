package com.cdy.simplerpc.registry.zookeeper;

import com.cdy.simplerpc.registry.IServiceDiscovery;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务发现
 * Created by 陈东一
 * 2018/9/1 21:31
 */
public class ZKServiceDiscoveryImpl implements IServiceDiscovery {
    
    CuratorFramework curatorFramework;
    List<String> list = new ArrayList<>();
    
    public ZKServiceDiscoveryImpl() {
        curatorFramework = CuratorFrameworkFactory.builder().connectString(ZKConfig.zkAddress)
                .sessionTimeoutMs(4000)
                .retryPolicy(new ExponentialBackoffRetry(10000, 5))
                .build();
        curatorFramework.start();
    }
    
    @Override
    public String discovery(String serviceName) {
        String path = ZKConfig.zkRegistryPath + "/" + serviceName;
        //发现地址
        try {
            list = curatorFramework.getChildren().forPath(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //负载均衡
        return loadBalance(list);
    }
    
    private String loadBalance(List<String> list) {
        System.out.println("可用地址"+list);
        if (list == null || list.isEmpty()) {
            throw new RuntimeException("没有可用的地址");
        }
        if (list.size() == 1) {
            return list.get(0);
        } else {
            double v = Math.random() * list.size();
            return list.get((int) v);
        }
    }
}
