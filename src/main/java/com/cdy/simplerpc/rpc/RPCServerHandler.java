package com.cdy.simplerpc.rpc;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

/**
 * 服务端消息处理
 * Created by 陈东一
 * 2018/9/1 22:00
 */
@Slf4j
public class RPCServerHandler<T> extends SimpleChannelInboundHandler<T> {
    
    private Supplier<HandlerProcessor<T>> supplier;

    
    public RPCServerHandler(Supplier<HandlerProcessor<T>> supplier) {
        this.supplier = supplier;
    }
    
    @Override
    public void channelRead0(ChannelHandlerContext ctx, T t) {
        supplier.get().process(t, ctx);
    }
}
