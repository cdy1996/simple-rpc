package com.cdy.simplerpc.rpc;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

@ChannelHandler.Sharable
public class RPCPackageServerDecoder extends MessageToMessageDecoder<RPCPackage> {

    @Override
    protected void decode(ChannelHandlerContext ctx, RPCPackage msg, List<Object> out) throws Exception {
        RPCContext context = new RPCContext(msg.getRequestId(), ctx, msg.getTarget());
        context.setAttach(msg.getMap());
        RPCContext.set(context); //服务端接收并传递上下文
        out.add(context);
    }
}
