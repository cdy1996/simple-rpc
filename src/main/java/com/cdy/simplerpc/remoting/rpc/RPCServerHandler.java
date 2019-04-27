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

import static com.cdy.simplerpc.remoting.rpc.RPCServer.handlerMap;

/**
 * 服务端消息处理
 * Created by 陈东一
 * 2018/9/1 22:00
 */
@ChannelHandler.Sharable
@Slf4j
public class RPCServerHandler extends SimpleChannelInboundHandler<RPCRequest> {
    
    
    @Override
    public void channelRead0(ChannelHandlerContext ctx, RPCRequest msg1) throws Exception {
        log.debug("接受到请求" + msg1);
        
        String className = msg1.getClassName();
        Object result = null;
        RPCResponse rpcResponse = new RPCResponse();
        if (handlerMap.containsKey(className)) {
            Invoker o = handlerMap.get(className);
            Invocation invocation = new Invocation(msg1.getMethodName(), msg1.getParams(), msg1.getTypes());
            invocation.setAttach(msg1.getAttach());
            result = o.invoke(invocation);
        }
        RPCContext rpcContext = RPCContext.current();
        rpcResponse.setAttach(rpcContext.getMap());
        rpcResponse.setRequestId(msg1.getRequestId());
        rpcResponse.setResultData(result);
        ctx.writeAndFlush(rpcResponse);
    }
}
