package com.cdy.simplerpc.registry.zookeeper;

import com.cdy.simplerpc.balance.IBalance;
import com.cdy.simplerpc.registry.AbstractDiscovery;
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
public class ZKServiceDiscoveryImpl extends AbstractDiscovery {
    
    CuratorFramework curatorFramework;
    List<String> list = new ArrayList<>();
    
    
    public ZKServiceDiscoveryImpl(IBalance balance) {
        super(balance);
        init();
    }
    
    public void init(){
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
    
   
}
