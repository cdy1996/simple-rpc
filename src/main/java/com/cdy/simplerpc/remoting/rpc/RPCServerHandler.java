package com.cdy.simplerpc.remoting.rpc;

import com.cdy.simplerpc.proxy.Invocation;
import com.cdy.simplerpc.proxy.Invoker;
import com.cdy.simplerpc.remoting.RPCContext;
import com.cdy.simplerpc.remoting.RPCRequest;
import com.cdy.simplerpc.remoting.RPCResponse;
import com.cdy.simplerpc.serialize.ISerialize;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 服务端消息处理
 * Created by 陈东一
 * 2018/9/1 22:00
 */
@Slf4j
@ChannelHandler.Sharable
public class RPCServerHandler extends SimpleChannelInboundHandler<byte[]> {
    
    private final Map<String, Invoker> handlerMap;
    private final ISerialize serialize;
    
    public RPCServerHandler(Map<String, Invoker> handlerMap, ISerialize serialize) {
        this.handlerMap = handlerMap;
        this.serialize = serialize;
    }
    
    @Override
    public void channelRead0(ChannelHandlerContext ctx, byte[] msg1) throws Exception {
        RPCRequest request = (RPCRequest) serialize.deserialize(msg1, RPCRequest.class);
        log.info("接受到请求" + request);
    
        RPCContext context = RPCContext.current();
        Map<String, Object> contextMap = context.getMap();
        
        String className = request.getClassName();
        Object result = null;
        if (handlerMap.containsKey(className)) {
            Invoker o = handlerMap.get(className);
            Invocation invocation = new Invocation(request.getMethodName(), request.getParams(), request.getTypes());
            //接受传过来的上下文
            contextMap.putAll(request.getAttach());
            result = o.invoke(invocation);
        }
        
        RPCResponse rpcResponse = new RPCResponse();
        rpcResponse.setAttach(contextMap);
        rpcResponse.setRequestId(request.getRequestId());
        rpcResponse.setResultData(result);
        ctx.writeAndFlush(rpcResponse);
    }
}
