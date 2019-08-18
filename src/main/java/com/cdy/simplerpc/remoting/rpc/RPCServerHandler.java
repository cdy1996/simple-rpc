package com.cdy.simplerpc.remoting.rpc;

import com.cdy.simplerpc.proxy.Invocation;
import com.cdy.simplerpc.proxy.Invoker;
import com.cdy.simplerpc.remoting.RPCContext;
import com.cdy.simplerpc.remoting.RPCRequest;
import com.cdy.simplerpc.remoting.RPCResponse;
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
public class RPCServerHandler extends SimpleChannelInboundHandler<RPCRequest> {
    
    private final Map<String, Invoker> handlerMap;
    
    public RPCServerHandler(Map<String, Invoker> handlerMap) {
        this.handlerMap = handlerMap;
    }
    
    @Override
    public void channelRead0(ChannelHandlerContext ctx, RPCRequest request) {
        log.info("接受到请求 {}",  request);
    
        RPCResponse rpcResponse = new RPCResponse();
        try {
            RPCContext context = RPCContext.current();
            Map<String, Object> contextMap = context.getMap();
            rpcResponse.setAttach(contextMap);
            rpcResponse.setRequestId(request.getRequestId());
            
            String className = request.getClassName();
            Object result = null;
            if (handlerMap.containsKey(className)) {
                Invoker o = handlerMap.get(className);
                Invocation invocation = new Invocation(request.getMethodName(), request.getParams(), request.getTypes());
                //接受传过来的上下文
                contextMap.putAll(request.getAttach());
                result = o.invoke(invocation);
            }
            
            rpcResponse.setResultData(result);
            ctx.writeAndFlush(rpcResponse);
        } catch (Exception e) {
            rpcResponse.setResultData(e);
            ctx.writeAndFlush(rpcResponse);
        }
    }
}
