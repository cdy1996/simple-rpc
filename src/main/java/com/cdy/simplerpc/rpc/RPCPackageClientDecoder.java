package com.cdy.simplerpc.rpc;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

@ChannelHandler.Sharable
public class RPCPackageClientDecoder extends MessageToMessageDecoder<RPCPackage> {

    @Override
    protected void decode(ChannelHandlerContext ctx, RPCPackage msg, List<Object> out) throws Exception {
        RPCContext context = new RPCContext(msg.getRequestId(), ctx, msg.getTarget());
        out.add(context);
    }
}
