package com.cdy.simplerpc.balance;

import com.netflix.client.DefaultLoadBalancerRetryHandler;
import com.netflix.loadbalancer.*;
import com.netflix.loadbalancer.reactive.LoadBalancerCommand;
import rx.Observable;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.cdy.simplerpc.util.StringUtil.getServer;
import static com.cdy.simplerpc.util.StringUtil.toServer;

/**
 * ribbon实现负载均衡
 * Created by 陈东一
 * 2019/2/7 0007 20:14
 */
public class RibbonBalance implements IBalance {
    
    private IRule iRule = new RandomRule();
    
    private static ConcurrentHashMap<String, BaseLoadBalancer> loadBalancerMap
            = new ConcurrentHashMap<>();
    
    
    public IRule getiRule() {
        return iRule;
    }
    
    @Override
    public void setiRule(IRule iRule) {
        this.iRule = iRule;
    }
    
    @Override
    public void addServer(String serviceName, List<String> servers) {
        loadBalancerMap.computeIfPresent(serviceName, (k, v) -> {
            servers.forEach(e -> v.addServer(toServer(getServer(e))));
            return v;
        });
    }
    
    @Override
    public void deleteServer(String serviceName, List<String> servers) {
        if (servers == null) {
            loadBalancerMap.remove(serviceName);
        } else {
            loadBalancerMap.computeIfPresent(serviceName, (k, v) -> {
                servers.forEach(e -> v.markServerDown(toServer(getServer(e))));
                return v;
            });
        }
        
        
    }
    
    @Override
    public String loadBalance(String serviceName, List<String> list) {
        BaseLoadBalancer baseLoadBalancer = loadBalancerMap.getOrDefault(serviceName, generateBalancer(list));
        Server server = baseLoadBalancer.chooseServer();
        return server.getHost() + ":" + server.getPort();
    }
    
    public BaseLoadBalancer generateBalancer(List<String> list) {
        List<Server> collect = list.stream().map(e -> toServer(getServer(e))).collect(Collectors.toList());
        
        
        return LoadBalancerBuilder.newBuilder()
                .withRule(iRule)
                .withPing(new DummyPing())
                .buildFixedServerListLoadBalancer(collect);
    }
    
    public String call(BaseLoadBalancer baseLoadBalancer) {
        
        LoadBalancerCommand<String> command = LoadBalancerCommand.<String>builder()
                .withLoadBalancer(baseLoadBalancer)
                .withRetryHandler(new DefaultLoadBalancerRetryHandler(0, 1, true))
                .build();
        
        return command.submit(server -> Observable.just(server.getHost() + ":" + server.getPort()))
                .toBlocking().first();
    }
}
