package com.cdy.simplerpc.balance;

import com.cdy.simplerpc.exception.DiscoveryException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 简单负载均衡
 * Created by 陈东一
 * 2019/2/7 0007 20:13
 */
@Slf4j
public class SimpleBalance implements IBalance{

    
    @Override
    public void addServer(String serviceName, List<String> servers) {
    
    }
    
    @Override
    public void deleteServer(String serviceName, List<String> servers) {
    
    }
    
    @Override
    public String loadBalance(String serviceName, List<String> list) {
        log.debug("可用地址"+list);
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
