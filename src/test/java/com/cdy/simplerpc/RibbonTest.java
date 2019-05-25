package com.cdy.simplerpc;

import com.netflix.client.ClientFactory;
import com.netflix.client.DefaultLoadBalancerRetryHandler;
import com.netflix.client.http.HttpRequest;
import com.netflix.client.http.HttpResponse;
import com.netflix.config.ConfigurationManager;
import com.netflix.loadbalancer.*;
import com.netflix.loadbalancer.reactive.LoadBalancerCommand;
import com.netflix.loadbalancer.reactive.ServerOperation;
import com.netflix.niws.client.http.RestClient;
import lombok.extern.slf4j.Slf4j;
import rx.Observable;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * ribbon测试
 * Created by 陈东一
 * 2019/2/7 0007 19:51
 */
@Slf4j
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
                    connection.setReadTimeout(5000);
                    return Observable.just(connection.getResponseMessage());
                } catch (IOException e) {
                    return Observable.error(e);
                }
            }
        }).toBlocking().first();
    }
    
    public String test() throws Exception {
        // 1、设置请求的服务器
        ConfigurationManager.getConfigInstance().setProperty("sample-clien.ribbon.listOfServers",
                "localhost:8091,localhost:8092"); // 1
        // 2、 配置规则处理类
        //本示例略，先默认使用其默认负载均衡策略规则
//        ConfigurationManager.getConfigInstance().setProperty("sample-clien.ribbon.NFLoadBalancerRuleClassName",
//                MyProbabilityRandomRule.class.getName());
        
        // 3、获取 REST 请求客户端
//        LoadBalancingHttpClient<ByteBuf, ByteBuf> httpClient = RibbonTransport.newHttpClient();
        RestClient client = (RestClient) ClientFactory.getNamedClient("sample-clien");
        
        
        // 4、创建请求实例
        HttpRequest request = HttpRequest.newBuilder().uri("/carsInfo/onsale").build();
        
        // 5、发 送 10 次请求到服务器中
        for (int i = 0; i < 10; i++) {
            log.info("the "+(i+1)+"th: ");
            HttpResponse response = client.executeWithLoadBalancer(request);
            String result = response.getEntity(String.class);
            log.info(result);
        }
        return null;
    }
}
