package com.cdy.simplerpc.remoting.rpc;

import com.cdy.simplerpc.config.PropertySources;
import com.cdy.simplerpc.proxy.Invocation;
import com.cdy.simplerpc.proxy.Invoker;
import com.cdy.simplerpc.registry.IServiceRegistry;
import com.cdy.simplerpc.remoting.AbstractServer;
import com.cdy.simplerpc.remoting.RPCRequest;
import com.cdy.simplerpc.remoting.RPCResponse;
import com.cdy.simplerpc.remoting.ServerMetaInfo;
import com.cdy.simplerpc.rpc.NettyServer;
import com.cdy.simplerpc.serialize.ISerialize;
import com.cdy.simplerpc.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static com.cdy.simplerpc.util.StringUtil.getServer;

/**
 * 服务端
 * Created by 陈东一
 * 2018/9/1 21:41
 */
@Slf4j
public class RPCServer extends AbstractServer {

    NettyServer<RPCRequest,RPCResponse> nettyServer;

    public RPCServer(ServerMetaInfo serverMetaInfo, List<IServiceRegistry> registry, ISerialize serialize, PropertySources propertySource) {
        super(serverMetaInfo, registry, serialize, propertySource);
    }
    
    @Override
    public void openServer() throws Exception {
        nettyServer = new NettyServer<>();
        nettyServer.setSerialize(getSerialize());
        nettyServer.addProcessor((request, ctx)->{
            log.info("接受到请求 {}", request);
            RPCResponse rpcResponse = new RPCResponse();
            try {
                String className = request.getClassName();
                Object result = null;
                if (getHandlerMap().containsKey(className)) {
                    Invoker o = getHandlerMap().get(className);
                    Invocation invocation = new Invocation(request.getMethodName(), request.getParams(), request.getTypes());
                    result = o.invoke(invocation);
                }
                rpcResponse.setResultData(result);
            } catch (Exception e) {
                rpcResponse.setResultData(e);
            }
            return rpcResponse;
        });
        
        StringUtil.TwoResult<String, Integer> server = getServer(getAddress());
        String ip = server.getFirst();
        int port = server.getSecond();
        nettyServer.openServer(ip, port);
        
    }
    
    @Override
    public void close() {
        nettyServer.close();
    }
    
}
