package com.cdy.simplerpc.remoting.netty;

import com.cdy.simplerpc.remoting.RPCRequest;
import com.cdy.simplerpc.remoting.RPCResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * 服务端消息处理
 * Created by 陈东一
 * 2018/9/1 22:00
 */
public class RPCHandler extends SimpleChannelInboundHandler<RPCRequest> {
    private HashMap<String, Object> handlerMap;
    
    public RPCHandler(HashMap<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }
    
    @Override
    public void channelRead0(ChannelHandlerContext ctx, RPCRequest msg1) throws Exception {
        System.out.println("接受到请求"+msg1);
    
        String className = msg1.getClassName();
        Object result = null;
        RPCResponse rpcResponse = new RPCResponse();
        if(handlerMap.containsKey(className)){
            Object o = handlerMap.get(className);
            Method method = o.getClass().getMethod(msg1.getMethodName(),msg1.getTypes());
            result = method.invoke(o, msg1.getParams());
          
        }
        rpcResponse.setRequestId(msg1.getRequestId());
        rpcResponse.setResultData(result);
        ctx.writeAndFlush(rpcResponse);
        
    }
}
