package com.cdy.simplerpc.remoting.rpc;

import com.cdy.simplerpc.proxy.Invocation;
import com.cdy.simplerpc.proxy.Invoker;
import com.cdy.simplerpc.registry.IServiceRegistry;
import com.cdy.simplerpc.remoting.*;
import com.cdy.simplerpc.serialize.ISerialize;
import com.cdy.simplerpc.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

import static com.cdy.simplerpc.util.StringUtil.getServer;

/**
 * 服务端
 * Created by 陈东一
 * 2018/9/1 21:41
 */
@Slf4j
public class RPCServer extends AbstractServer {
    com.cdy.simplerpc.rpc.RPCServer<RPCPackage> rpcServer;
   
   
    public RPCServer(ServerMetaInfo serverMetaInfo, List<IServiceRegistry> registry, ISerialize serialize) {
        super(serverMetaInfo, registry, serialize);
    }
    
    @Override
    public void openServer() throws Exception {
        rpcServer = new com.cdy.simplerpc.rpc.RPCServer();
        rpcServer.setSerialize(getSerialize());
//        rpcServer.setEncoder( new SerializeCoderFactory.SerializeEncoderHandler(serialize, RPCPackage.class));
//        rpcServer.setDencoder( new SerializeCoderFactory.SerializeDecoderHandler(serialize, RPCPackage.class));
        rpcServer.addProcessor((t,ctx)->{
            RPCRequest request = (RPCRequest) t.getTarget();
            log.info("接受到请求 {}",  request);
    
            RPCResponse rpcResponse = new RPCResponse();
            try {
                RPCContext context = RPCContext.current();
                Map<String, Object> contextMap = context.getMap();
                rpcResponse.setAttach(contextMap);
                rpcResponse.setRequestId(request.getRequestId());
        
                String className = request.getClassName();
                Object result = null;
                if (getHandlerMap().containsKey(className)) {
                    Invoker o = getHandlerMap().get(className);
                    Invocation invocation = new Invocation(request.getMethodName(), request.getParams(), request.getTypes());
                    //接受传过来的上下文
                    contextMap.putAll(request.getAttach());
                    result = o.invoke(invocation);
                }
                rpcResponse.setResultData(result);
                ctx.writeAndFlush(new RPCPackage(rpcResponse));
            } catch (Exception e) {
                rpcResponse.setResultData(e);
                ctx.writeAndFlush(rpcResponse);
            }
        });
        
        StringUtil.TwoResult<String, Integer> server = getServer(getAddress());
        String ip = server.getFirst();
        int port = server.getSecond();
        rpcServer.openServer(ip, port);
        
    }
    
    @Override
    public void close() {
        rpcServer.close();
    }
    
}
