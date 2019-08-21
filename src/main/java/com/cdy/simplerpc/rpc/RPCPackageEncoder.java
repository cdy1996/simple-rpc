package com.cdy.simplerpc.rpc;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

@ChannelHandler.Sharable
public class RPCPackageEncoder extends MessageToMessageEncoder<RPCContext> {

    @Override
    protected void encode(ChannelHandlerContext ctx, RPCContext context, List<Object> out) throws Exception {
        out.add(new RPCPackage(context.getRequestId(), context.getTarget(), context.getAttach()));
    }

}
