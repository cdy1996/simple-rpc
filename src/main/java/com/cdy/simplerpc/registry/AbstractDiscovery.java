package com.cdy.simplerpc.registry;

import com.cdy.simplerpc.balance.IBalance;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务发现 负载均衡抽象层
 * Created by 陈东一
 * 2018/9/1 21:31
 */
public abstract class AbstractDiscovery implements IServiceDiscovery{
    
    private IBalance balance;
    
    public AbstractDiscovery(IBalance balance) {
        this.balance = balance;
    }
    
    //缓存所有的服务对应的地址列表
    @Getter
    private Map<String, List<String>> cache = new ConcurrentHashMap<>();
    
    
    
    protected String loadBalance(String serviceName, List<String> list) {
        return balance.loadBalance(serviceName, list);
    }
    
    
}
