package com.cdy.simplerpc.remoting.rpc;

import com.cdy.simplerpc.remoting.RPCFuture;
import com.cdy.simplerpc.remoting.RPCResponse;
import com.cdy.simplerpc.serialize.ISerialize;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.Attribute;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import static com.cdy.simplerpc.remoting.rpc.RPCClient.ATTRIBUTE_KEY_ADDRESS;

/**
 * 客户端消息处理
 * Created by 陈东一
 * 2018/9/1 22:24
 */
@Slf4j
public class RPCClientHandler extends SimpleChannelInboundHandler<byte[]> {
    

    private final Map<String, Channel> addressChannel ;
    private final Map<String, RPCFuture> responseFuture ;
    private final ISerialize serialize;
    
    public RPCClientHandler(Map<String, Channel> addressChannel, Map<String, RPCFuture> responseFuture, ISerialize serialize) {
        this.addressChannel = addressChannel;
        this.responseFuture = responseFuture;
        this.serialize = serialize;
    }
    
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        Attribute<String> attr = channel.attr(ATTRIBUTE_KEY_ADDRESS);
        String address = attr.get();
        addressChannel.remove(address);
        super.channelInactive(ctx);
    }
    
    @Override
    public void channelRead0(ChannelHandlerContext ctx, byte[] msg) throws Exception {
        RPCResponse rpcresponse = (RPCResponse) serialize.deserialize(msg, RPCResponse.class);
        log.info("接收到内容" + rpcresponse);
        RPCFuture rpcFuture = responseFuture.remove(rpcresponse.getRequestId());
        if(rpcFuture == null){
            return;
        }
        rpcFuture.setAttach(rpcresponse.getAttach());
        rpcFuture.setResultData(rpcresponse.getResultData());
        
    }
}
