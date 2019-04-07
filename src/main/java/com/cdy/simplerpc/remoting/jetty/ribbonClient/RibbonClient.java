package com.cdy.simplerpc.remoting.jetty.ribbonClient;

import com.cdy.serialization.JsonUtil;
import com.cdy.simplerpc.annotation.ReferenceMetaInfo;
import com.cdy.simplerpc.balance.IBalance;
import com.cdy.simplerpc.balance.RibbonBalance;
import com.cdy.simplerpc.proxy.Invocation;
import com.cdy.simplerpc.registry.AbstractDiscovery;
import com.cdy.simplerpc.remoting.AbstractClient;
import com.cdy.simplerpc.remoting.RPCContext;
import com.cdy.simplerpc.remoting.RPCRequest;
import com.cdy.simplerpc.remoting.RPCResponse;
import com.cdy.simplerpc.remoting.jetty.httpClient.HttpClientUtil;
import com.netflix.client.DefaultLoadBalancerRetryHandler;
import com.netflix.loadbalancer.*;
import com.netflix.loadbalancer.reactive.LoadBalancerCommand;
import org.apache.http.impl.client.CloseableHttpClient;
import rx.Observable;

import java.util.List;
import java.util.stream.Collectors;

import static com.cdy.simplerpc.remoting.jetty.httpClient.HttpClientUtil.getHttpClient;
import static com.cdy.simplerpc.util.StringUtil.getServer;
import static com.cdy.simplerpc.util.StringUtil.toServer;

/**
 * todo
 * Created by 陈东一
 * 2019/3/3 0003 16:26
 */
public class RibbonClient extends AbstractClient {
    
    
    @Override
    public Object invoke(Invocation invocation) throws Exception {
        RPCRequest rpcRequest = invocation.toRequest();
        
        String serviceName = invocation.getInterfaceClass().getName();
        List<String> address = getServiceDiscovery().listServer(serviceName);
        
        BaseLoadBalancer baseLoadBalancer = generateBalancer(address);
        
        // 隐式传递参数
        RPCContext rpcContext1 = RPCContext.current();
        rpcRequest.setAttach(rpcContext1.getMap());
        rpcRequest.getAttach().put("address", address);
        
        ReferenceMetaInfo referenceMetaInfo = getClientBootStrap().getReferenceMetaInfo(serviceName);
        
        if (referenceMetaInfo.isAsync()) {
            return call(rpcRequest, baseLoadBalancer, referenceMetaInfo).toBlocking().toFuture();
        } else {
            return call(rpcRequest, baseLoadBalancer, referenceMetaInfo).toBlocking().first();
        }
        
    }
    
    @Override
    public void close() {
    }
    
    public Observable<Object> call(RPCRequest rpcRequest, BaseLoadBalancer baseLoadBalancer,  ReferenceMetaInfo referenceMetaInfo) {
        
        LoadBalancerCommand<String> command = LoadBalancerCommand.<String>builder()
                .withLoadBalancer(baseLoadBalancer)
                .withRetryHandler(new DefaultLoadBalancerRetryHandler(0, 1, true))
                .build();
        RPCContext current = RPCContext.current();
        return command.submit(server -> {
            try {
                CloseableHttpClient client = getHttpClient();
                // 隐式传递参数
                RPCContext rpcContext1 = RPCContext.current();
                rpcRequest.setAttach(rpcContext1.getMap());
                rpcRequest.getAttach().put("address", server.getHost() + ":" + server.getPort());
                
                
                return Observable.just(HttpClientUtil.execute(client,
                        "http://" + server.getHost() + ":" + server.getPort() + "/simpleRPC",
                        JsonUtil.toString(rpcRequest),
                        Math.toIntExact(referenceMetaInfo.getTimeout())));
            } catch (Exception e) {
                return Observable.error(e);
            }
        }).map(result -> {
            RPCResponse rpcResponse = JsonUtil.parseObject(result, RPCResponse.class);
            // 隐式接受参数
            current.setMap(rpcResponse.getAttach());
            return rpcResponse.getResultData();
        });
    }
    
    
    public BaseLoadBalancer generateBalancer(List<String> list) {
        List<Server> collect = list.stream().map(e -> toServer(getServer(e))).collect(Collectors.toList());
        
        IRule iRule;
        try {
            IBalance balance = ((AbstractDiscovery) getServiceDiscovery()).getBalance();
            iRule = ((RibbonBalance) balance).getiRule();
        } catch (Exception e) {
            iRule = new RandomRule();
        }
        
        return LoadBalancerBuilder.newBuilder()
                .withRule(iRule)
                .withPing(new DummyPing())
                .buildFixedServerListLoadBalancer(collect);
    }
    
}
