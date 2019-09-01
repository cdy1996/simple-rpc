package com.cdy.simplerpc.rpc;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@ChannelHandler.Sharable
@Slf4j
public class RPCPackageClientDecoder extends MessageToMessageDecoder<RPCPackage> {

    @Override
    protected void decode(ChannelHandlerContext ctx, RPCPackage msg, List<Object> out) throws Exception {
        log.info("RPCPackage解包 -> " + msg);
        RPCContext context = new RPCContext(msg.getRequestId(), ctx, msg.getTarget());
        out.add(context);
    }
}
