package com.cdy.simplerpc.balance;

import com.netflix.client.DefaultLoadBalancerRetryHandler;
import com.netflix.loadbalancer.*;
import com.netflix.loadbalancer.reactive.LoadBalancerCommand;
import rx.Observable;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ribbon实现负载均衡
 * Created by 陈东一
 * 2019/2/7 0007 20:14
 */
public class RibbonBalance implements IBalance{
    
    IRule iRule;
    
    public RibbonBalance(IRule iRule) {
        this.iRule = iRule;
    }
    
    @Override
    public String loadBalance(List<String> list) {
    
        List<Server> collect = list.stream().map(e -> {
            String[] split = e.split(":");
            return new Server(split[0], Integer.valueOf(split[1]));
        }).collect(Collectors.toList());
    
        BaseLoadBalancer baseLoadBalancer = LoadBalancerBuilder.newBuilder()
                .withRule(iRule)
                .buildFixedServerListLoadBalancer(collect);
        Server server = baseLoadBalancer.chooseServer();
    
        return server.getHost()+":"+server.getPort();
    }
    
    public String call(BaseLoadBalancer baseLoadBalancer){
        
        LoadBalancerCommand<String> command = LoadBalancerCommand.<String>builder()
                .withLoadBalancer(baseLoadBalancer)
                .withRetryHandler(new DefaultLoadBalancerRetryHandler(0, 1, true))
                .build();
    
        return command.submit(server -> Observable.just(server.getHost() + ":" + server.getPort())).toBlocking().first();
    }
}
