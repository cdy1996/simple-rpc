package com.cdy.simplerpc.remoting.http;

import com.alibaba.nacos.client.utils.JSONUtils;
import com.cdy.simplerpc.config.ConfigConstants;
import com.cdy.simplerpc.config.PropertySources;
import com.cdy.simplerpc.exception.RPCException;
import com.cdy.simplerpc.proxy.Invocation;
import com.cdy.simplerpc.remoting.AbstractClient;
import com.cdy.simplerpc.remoting.RPCRequest;
import com.cdy.simplerpc.remoting.RPCResponse;
import com.cdy.simplerpc.netty.rpc.RPCContext;
import com.cdy.simplerpc.serialize.ISerialize;
import com.cdy.simplerpc.serialize.JsonSerialize;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 客户端
 * Created by 陈东一
 * 2018/11/25 0025 14:28
 */
public class HTTPClient extends AbstractClient {
    
    
    public HTTPClient(PropertySources propertySources, ISerialize serialize) {
        super(propertySources, serialize);
    }
    
    @Override
    public Object invoke(Invocation invocation) throws Exception {
       
   
        RPCContext context = RPCContext.current();
        Map<String, Object> contextMap = context.getAttach();
        Map<String, String> header = new HashMap<>();
        //todo
        header.put("context", JSONUtils.serializeObject(contextMap));
    
        // 隐式传递参数
        RPCRequest rpcRequest = invocation.toRequest();
//        rpcRequest.setAttach(contextMap);
        
        String address = (String) contextMap.get(RPCContext.address);

        String annotationKey = (String) contextMap.get(RPCContext.annotationKey);
        String async = propertySources.resolveProperty(annotationKey + "." + ConfigConstants.async);
        String timeout = propertySources.resolveProperty(annotationKey + "." + ConfigConstants.timeout);
    
        if ("true".equalsIgnoreCase(async)) {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return send(rpcRequest, address, timeout, header);
                } catch (Exception e) {
                    throw new RPCException(e);
                }
            });
        } else {
            return send(rpcRequest, address, timeout, header);
        }
       
    }
    
    private Object send(RPCRequest rpcRequest, String address, String timeout, Map<String, String> header) throws Exception {
        byte[] serialize = getSerialize().serialize(rpcRequest, RPCRequest.class);
        byte[] result;
        if (getSerialize() instanceof JsonSerialize){
            result = HttpClientUtil.execute(
                    "http://" +address + "/simpleRPC",
                    header,
                    new String(serialize, StandardCharsets.UTF_8),
                    Integer.valueOf(timeout));
        } else {
            result = HttpClientUtil.execute(
                    "http://" + address + "/simpleRPC",
                    header,
                    serialize,
                    Integer.valueOf(timeout));
        }
    
    
        RPCResponse rpcResponse = getSerialize().deserialize(result, RPCResponse.class);

        // 隐式接受参数 返回时不用接收
//        context.setAttach(rpcResponse.getAttach());
        return rpcResponse.getResultData();
    }
    
    
    @Override
    public void close() {
        HttpClientUtil.close();
    }
    
}
