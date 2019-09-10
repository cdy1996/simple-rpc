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
    
    /**
     * 缓存所有的服务对应的地址列表
     *
     *  key -> serviceName value -> 服务地址列表
     * 设计成static, 是为了让多个服务发现共用一个缓存
     */
    @Getter
    private Map<String, List<String>> cache = new ConcurrentHashMap<>();
    
    
    
    protected String loadBalance(String serviceName, List<String> list) {
        return balance.loadBalance(serviceName, list);
    }
    
    
}
