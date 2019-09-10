package com.cdy.simplerpc.remoting.rpc;

import com.cdy.simplerpc.config.PropertySources;
import com.cdy.simplerpc.netty.rpc.NettyServer;
import com.cdy.simplerpc.proxy.Invoker;
import com.cdy.simplerpc.registry.IServiceRegistry;
import com.cdy.simplerpc.remoting.AbstractServer;
import com.cdy.simplerpc.remoting.RPCRequest;
import com.cdy.simplerpc.remoting.RPCResponse;
import com.cdy.simplerpc.remoting.ServerMetaInfo;
import com.cdy.simplerpc.serialize.ISerialize;
import com.cdy.simplerpc.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static com.cdy.simplerpc.util.StringUtil.getServer;

/**
 * 服务端
 * 不同服务的相同端口号和协议
 * Created by 陈东一
 * 2018/9/1 21:41
 */
@Slf4j
public class RPCServer extends AbstractServer {


    private NettyServer<RPCRequest, RPCResponse> nettyServer;

    public RPCServer(ServerMetaInfo serverMetaInfo, List<IServiceRegistry> registry, ISerialize serialize, PropertySources propertySource) {
        super(serverMetaInfo, registry, serialize, propertySource);
    }

    @Override
    public void openServer() throws Exception {
        if (open) {
            return;
        }
        open = true;
        nettyServer = new NettyServer<>();
        nettyServer.setSerialize(getSerialize());
        nettyServer.addProcessor((request, ctx) -> {
            log.info("接受到请求 {}", request);
            RPCResponse rpcResponse = new RPCResponse();
            try {
                String className = request.getClassName();
                Object result = null;
                if (getHandlerMap().containsKey(className)) {
                    Invoker o = getHandlerMap().get(className);
                    result = o.invoke(request.toInvocation());
                }
                rpcResponse.setResultData(result);
            } catch (Exception e) {
                rpcResponse.setResultData(e);
            }
            return rpcResponse;
        });

        StringUtil.TwoResult<String, Integer> serverAddress = getServer(getAddress());
        String ip = serverAddress.getFirst();
        int port = serverAddress.getSecond();
        nettyServer.openServer(ip, port);

    }

    @Override
    public void close() {
        open = false;
        nettyServer.close();
    }

}
