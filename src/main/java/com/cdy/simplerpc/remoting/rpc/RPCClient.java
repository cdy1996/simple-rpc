package com.cdy.simplerpc.remoting.rpc;

import com.cdy.simplerpc.config.ConfigConstants;
import com.cdy.simplerpc.config.PropertySources;
import com.cdy.simplerpc.proxy.Invocation;
import com.cdy.simplerpc.remoting.AbstractClient;
import com.cdy.simplerpc.remoting.RPCRequest;
import com.cdy.simplerpc.remoting.RPCResponse;
import com.cdy.simplerpc.netty.rpc.NettyClient;
import com.cdy.simplerpc.netty.rpc.RPCContext;
import com.cdy.simplerpc.serialize.ISerialize;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

/**
 * 客户端
 * Created by 陈东一
 * 2018/11/25 0025 14:28
 */
public class RPCClient extends AbstractClient {

    ConcurrentHashMap<String, NettyClient<RPCRequest, RPCResponse>> clients = new ConcurrentHashMap<>();

    List<NettyClient<RPCRequest, RPCResponse>> clientList = new CopyOnWriteArrayList<>();

    public RPCClient(PropertySources propertySources, ISerialize serialize) {
        super(propertySources, serialize);
    }

    @Override
    public Object invoke(Invocation invocation) throws Exception {
        RPCContext context = RPCContext.current();
        Map<String, Object> contextMap = context.getAttach();
        RPCRequest rpcRequest = invocation.toRequest();
        String address = (String) contextMap.get(RPCContext.address);

        String annotationKey = (String) contextMap.get(RPCContext.annotationKey);
        String async = propertySources.resolveProperty(annotationKey + "." + ConfigConstants.async);
        String timeout = propertySources.resolveProperty(annotationKey + "." + ConfigConstants.timeout);

        if ("true".equalsIgnoreCase(async)) {
            return connect(address).invokeAsync(rpcRequest);
        } else {
            return  connect(address).invokeSync(rpcRequest, Long.parseLong(timeout), TimeUnit.SECONDS).getResultData();
        }
    }
    

    private NettyClient<RPCRequest, RPCResponse> connect(String address) throws Exception {
        NettyClient<RPCRequest, RPCResponse> client = clients.get(address);
        if (client == null){
            client = NettyClient.connect(address);
            clientList.add(client);
            clients.putIfAbsent(address, client);
        }

        return client;
    }
    
    @Override
    public void close() {
        clientList.forEach(NettyClient::close);
    }
    
}
