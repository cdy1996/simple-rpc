package com.cdy.simplerpc.remoting.rpc;

import com.cdy.simplerpc.remoting.RPCFuture;
import com.cdy.simplerpc.remoting.RPCResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.Attribute;
import lombok.extern.slf4j.Slf4j;

import static com.cdy.simplerpc.proxy.RemoteInvoker.responseFuture;
import static com.cdy.simplerpc.remoting.rpc.RPCClient.ATTRIBUTE_KEY_ADDRESS;
import static com.cdy.simplerpc.remoting.rpc.RPCClient.addressChannel;

/**
 * 客户端消息处理
 * Created by 陈东一
 * 2018/9/1 22:24
 */
@ChannelHandler.Sharable
@Slf4j
public class RPCClientHandler extends SimpleChannelInboundHandler<RPCResponse> {
    
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        Attribute<String> attr = channel.attr(ATTRIBUTE_KEY_ADDRESS);
        String address = attr.get();
        addressChannel.remove(address);
        super.channelInactive(ctx);
    }
    
    @Override
    public void channelRead0(ChannelHandlerContext ctx, RPCResponse msg) throws Exception {
        log.info("接收到内容" + msg);
        RPCFuture rpcFuture = responseFuture.remove(msg.getRequestId());
        if(rpcFuture == null){
            return;
        }
        rpcFuture.setAttach(msg.getAttach());
        rpcFuture.setResultData(msg.getResultData());
        
    }
}
