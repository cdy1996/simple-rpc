package com.cdy.simplerpc;

import com.netflix.client.DefaultLoadBalancerRetryHandler;
import com.netflix.loadbalancer.*;
import com.netflix.loadbalancer.reactive.LoadBalancerCommand;
import com.netflix.loadbalancer.reactive.ServerOperation;
import rx.Observable;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * todo
 * Created by 陈东一
 * 2019/2/7 0007 19:51
 */
public class RibbonTest {
    
    public static void main(String[] args) {
    
        List<Server> list = new ArrayList<>();
        list.add(new Server("127.0.0 1", 8899));
        list.add(new Server("127.0.0 1", 8888));
        BaseLoadBalancer baseLoadBalancer = LoadBalancerBuilder.newBuilder()
                .withRule(new RandomRule())
                .buildFixedServerListLoadBalancer(list);
    
    
        LoadBalancerStats loadBalancerStats = baseLoadBalancer.getLoadBalancerStats();
        
        
    }
    
    
    public String call(String path, BaseLoadBalancer baseLoadBalancer){
    
        LoadBalancerCommand<String> command = LoadBalancerCommand.<String>builder()
                .withLoadBalancer(baseLoadBalancer)
                .withRetryHandler(new DefaultLoadBalancerRetryHandler(0, 1, true))
                .build();
    
        return command.submit(new ServerOperation<String>() {
            @Override
            public Observable<String> call(Server server) {
                try {
                    URL url = new URL("http://" + server.getHost() + ":" + server.getPort()+path);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                
                
                    return Observable.just(connection.getResponseMessage());
                } catch (IOException e) {
                    return Observable.error(e);
                }
            }
        }).toBlocking().first();
    }
}
