package com.cdy.simplerpc.registry;

import com.cdy.simplerpc.balance.IBalance;

import java.util.List;

/**
 * 服务发现 负载均衡抽象层
 * Created by 陈东一
 * 2018/9/1 21:31
 */
public abstract class AbstractDiscovery implements IServiceDiscovery{
    
    private IBalance balance;
    
    protected String loadBalance(String serviceName, List<String> list) {
        return balance.loadBalance(serviceName, list);
    }
    
    @Override
    public void setBalance(IBalance balance) {
        this.balance = balance;
    }
    
    public IBalance getBalance() {
        return balance;
    }
}
