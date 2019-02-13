package com.cdy.simplerpc.balance;

import com.netflix.loadbalancer.IRule;

import java.util.List;

/**
 * 负载均衡
 * Created by 陈东一
 * 2019/2/7 0007 20:09
 */
public interface IBalance {
    
    void setiRule(IRule iRule);
    
    /**
     * 添加可用的实例
     *
     * 该方法存在线程安全问题,不要多线程并发调用
     *
     * @param serviceName
     * @param servers
     */
    void addServer(String serviceName, List<String> servers);
    
    /**
     * 删除实例
     *
     * 该方法存在线程安全问题,不要多线程并发调用
     *
     * @param serviceName
     * @param servers
     */
    void deleteServer(String serviceName, List<String> servers);
    
    /**
     * 负载均衡 获取一个可用服务
     * @param serviceName  服务名称
     * @param list  服务实例列表
     * @return  一个实例
     */
    String loadBalance(String serviceName, List<String> list);
}
