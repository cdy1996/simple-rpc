package com.cdy.simplerpc.remoting.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 客户端消息处理
 * Created by 陈东一
 * 2018/9/1 22:24
 */
public class RPCProxyHandler extends ChannelInboundHandlerAdapter {
    
    private Object response;
    
    public Object getResponse(){
    return response;
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        this.response = msg;
        System.out.println("接收到内容"+msg);
    }
}
