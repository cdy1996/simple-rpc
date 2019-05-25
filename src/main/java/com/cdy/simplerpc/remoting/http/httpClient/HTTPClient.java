package com.cdy.simplerpc.remoting.http.httpClient;

import com.cdy.serialization.JsonUtil;
import com.cdy.simplerpc.config.ConfigConstants;
import com.cdy.simplerpc.config.PropertySources;
import com.cdy.simplerpc.exception.RPCException;
import com.cdy.simplerpc.proxy.Invocation;
import com.cdy.simplerpc.proxy.InvokerInvocationHandler;
import com.cdy.simplerpc.remoting.AbstractClient;
import com.cdy.simplerpc.remoting.RPCContext;
import com.cdy.simplerpc.remoting.RPCRequest;
import com.cdy.simplerpc.remoting.RPCResponse;
import com.cdy.simplerpc.util.StringUtil;
import org.apache.http.impl.client.CloseableHttpClient;

import java.util.concurrent.CompletableFuture;

import static com.cdy.simplerpc.remoting.http.httpClient.HttpClientUtil.getHttpClient;
import static com.cdy.simplerpc.util.StringUtil.getServer;

/**
 * 客户端
 * Created by 陈东一
 * 2018/11/25 0025 14:28
 */
public class HTTPClient extends AbstractClient {
    
    
    public HTTPClient(PropertySources propertySources) {
        super(propertySources);
    }
    
    @Override
    public Object invoke(Invocation invocation) throws Exception {
        RPCRequest rpcRequest = invocation.toRequest();
        
        String address = invocation.getAddress();
        StringUtil.TwoResult<String, Integer> server = getServer(address);
        
        CloseableHttpClient client = getHttpClient();
        // 隐式传递参数
        RPCContext rpcContext1 = RPCContext.current();
        rpcRequest.setAttach(rpcContext1.getMap());
        rpcRequest.getAttach().put("address", address);
    
        String annotationKey = (String) invocation.getAttach().get(InvokerInvocationHandler.annotationKey);
        String async = propertySources.resolveProperty(annotationKey + "." + ConfigConstants.async);
        String timeout = propertySources.resolveProperty(annotationKey + "." + ConfigConstants.timeout);
    
        if (async.equalsIgnoreCase("true")) {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return send(rpcRequest, server, client, timeout);
                } catch (Exception e) {
                    throw new RPCException(e);
                }
            });
        } else {
            return send(rpcRequest, server, client, timeout);
        }
       
    }
    
    private Object send(RPCRequest rpcRequest, StringUtil.TwoResult<String, Integer> server, CloseableHttpClient client, String timeout) throws Exception {
        String result = HttpClientUtil.execute(client,
                "http://" + server.getFirst() + ":" + server.getSecond() + "/simpleRPC",
                JsonUtil.toString(rpcRequest),
                Integer.valueOf(timeout));
        
        
        RPCResponse rpcResponse = JsonUtil.parseObject(result, RPCResponse.class);
        // 隐式接受参数
        RPCContext rpcContext = RPCContext.current();
        rpcContext.setMap(rpcResponse.getAttach());
        return rpcResponse.getResultData();
    }
    
    
    @Override
    public void close() {
        HttpClientUtil.close();
    }
    
}
