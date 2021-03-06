package com.cdy.simplerpc.balance;

import com.cdy.simplerpc.event.Publisher;
import com.cdy.simplerpc.event.RPCEventListener;
import com.cdy.simplerpc.event.RemoveInvokerRefreshEvent;
import com.netflix.loadbalancer.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.cdy.simplerpc.util.StringUtil.getServerWithSchema;
import static com.cdy.simplerpc.util.StringUtil.toServer;

/**
 * ribbon实现负载均衡
 * Created by 陈东一
 * 2019/2/7 0007 20:14
 */
@Slf4j
public class RibbonBalance implements IBalance {
    
    private final IRule iRule;
    private final ConcurrentHashMap<String, BaseLoadBalancer> loadBalancerMap
            = new ConcurrentHashMap<>();
    
    
    public RibbonBalance() {
        this.iRule = new RandomRule();
    }
    public RibbonBalance(IRule iRule) {
        this.iRule = iRule;
    }
    
    
    @Override
    public String loadBalance(String serviceName, List<String> list) {
        log.info("可用地址 {}", list);
        BaseLoadBalancer baseLoadBalancer = loadBalancerMap.get(serviceName);
        if (baseLoadBalancer == null) {
            baseLoadBalancer = generateBalancer(list);
            loadBalancerMap.putIfAbsent(serviceName, baseLoadBalancer);
        }
        Server server = baseLoadBalancer.chooseServer();
        return server.getScheme()+"-"+server.getHost() + ":" + server.getPort();
    }
    
    private BaseLoadBalancer generateBalancer(List<String> list) {
        List<Server> collect = list.stream().map(e -> toServer(getServerWithSchema(e))).collect(Collectors.toList());
        
        
        return LoadBalancerBuilder.newBuilder()
                .withRule(iRule)
                .withPing(new DummyPing())
                .buildFixedServerListLoadBalancer(collect);
    }
    
    public void refresh(){
        Publisher publisher = new Publisher();
        publisher.registry((RPCEventListener<RemoveInvokerRefreshEvent>) eventObject -> {
        
        });
    }
    
    
    
    /**
     * 添加可用的实例
     *
     * 该方法存在线程安全问题,不要多线程并发调用
     *
     * @param serviceName
     * @param servers
     */
    public void addServer(String serviceName, List<String> servers) {
        loadBalancerMap.computeIfPresent(serviceName, (k, v) -> {
            servers.forEach(e -> v.addServer(toServer(getServerWithSchema(e))));
            return v;
        });
    }
    
    
    /**
     * 删除实例
     *
     * 该方法存在线程安全问题,不要多线程并发调用
     *
     * @param serviceName
     * @param servers
     */
    public void deleteServer(String serviceName, List<String> servers) {
        if (servers == null) {
            loadBalancerMap.remove(serviceName);
        } else {
            loadBalancerMap.computeIfPresent(serviceName, (k, v) -> {
                servers.forEach(e -> v.markServerDown(toServer(getServerWithSchema(e))));
                return v;
            });
        }
    }
    
    class RibbonServerRenewTask implements Runnable {
        @Override
        public void run() {
            //todo
        }
    }
}
