package com.cdy.simplerpc.remoting.netty;

import com.cdy.simplerpc.remoting.RPCRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * 服务端消息处理
 * Created by 陈东一
 * 2018/9/1 22:00
 */
public class RPCHandler extends ChannelInboundHandlerAdapter {
    private HashMap<String, Object> handlerMap;
    
    
    public RPCHandler(HashMap<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RPCRequest msg1 = (RPCRequest) msg;
        System.out.println("接受到请求"+msg1);
    
        String className = msg1.getClassName();
        Object result = null;
        if(handlerMap.containsKey(className)){
    
            Object o = handlerMap.get(className);
            Method method = o.getClass().getMethod(msg1.getMethodName(),msg1.getTypes());
            result = method.invoke(o, msg1.getParams());
        }
        
        ctx.write(result);
        ctx.flush();
        ctx.close();
    }
}
