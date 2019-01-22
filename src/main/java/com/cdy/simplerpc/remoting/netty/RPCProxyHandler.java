package com.cdy.simplerpc.remoting.netty;

import com.cdy.simplerpc.remoting.RPCFuture;
import com.cdy.simplerpc.remoting.RPCResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 客户端消息处理
 * Created by 陈东一
 * 2018/9/1 22:24
 */
public class RPCProxyHandler extends SimpleChannelInboundHandler<RPCResponse> {
    
    public static ConcurrentHashMap<String, RPCFuture> responseConcurrentHashMap = new ConcurrentHashMap<>();
    
    private ConcurrentHashMap<String, Channel> concurrentHashMap;
    
    public RPCProxyHandler(ConcurrentHashMap<String, Channel> concurrentHashMap) {
        this.concurrentHashMap = concurrentHashMap;
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        Set<Map.Entry<String, Channel>> entries = concurrentHashMap.entrySet();
        String key = null;
        for (Map.Entry<String, Channel> entry : entries) {
            if (entry.getValue().equals(channel)) {
                key = entry.getKey();
                break;
            }
        }
        if (key != null) {
            concurrentHashMap.remove(key);
        }
        super.channelInactive(ctx);
    }
    
    @Override
    public void channelRead0(ChannelHandlerContext ctx, RPCResponse msg) throws Exception {
        System.out.println("接收到内容" + msg);
        RPCFuture rpcFuture = responseConcurrentHashMap.remove(msg.getRequestId());
        rpcFuture.setResultData(msg.getResultData());
    }
}
