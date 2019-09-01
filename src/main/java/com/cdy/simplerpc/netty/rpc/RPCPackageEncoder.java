package com.cdy.simplerpc.netty.rpc;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@ChannelHandler.Sharable
@Slf4j
public class RPCPackageEncoder extends MessageToMessageEncoder<RPCContext> {

    @Override
    protected void encode(ChannelHandlerContext ctx, RPCContext context, List<Object> out) throws Exception {
        log.info("RPCContext打包 -> " + context);
        out.add(new RPCPackage(context.getRequestId(), context.getTarget(), context.getAttach()));
    }

}
