package com.cdy.simplerpc.balance;

import com.cdy.simplerpc.exception.DiscoveryException;
import com.netflix.loadbalancer.IRule;

import java.util.List;

/**
 * 简单负载均衡
 * Created by 陈东一
 * 2019/2/7 0007 20:13
 */
public class SimpleBalance implements IBalance{
    
    @Override
    public void setiRule(IRule iRule) {}
    
    @Override
    public void addServer(String serviceName, List<String> servers) {
    
    }
    
    @Override
    public void deleteServer(String serviceName, List<String> servers) {
    
    }
    
    @Override
    public String loadBalance(String serviceName, List<String> list) {
        System.out.println("可用地址"+list);
        if (list == null || list.isEmpty()) {
            throw new DiscoveryException("没有可用的地址");
        }
        if (list.size() == 1) {
            return list.get(0);
        } else {
            double v = Math.random() * list.size();
            return list.get((int) v);
        }
    }
}
