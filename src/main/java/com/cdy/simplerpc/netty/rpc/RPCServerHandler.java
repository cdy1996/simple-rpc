package com.cdy.simplerpc.netty.rpc;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * 服务端消息处理
 * Created by 陈东一
 * 2018/9/1 22:00
 */
@Slf4j
public class RPCServerHandler<T,R> extends SimpleChannelInboundHandler<RPCContext> {
    
    private Supplier<HandlerProcessor<T,R>> supplier;
    private AtomicReference<Supplier<HandlerProcessor<T,Void>>> supplierAtomicReference;

    
    public RPCServerHandler(Supplier<HandlerProcessor<T,R>> supplier) {
        this.supplier = supplier;
    }
    
    public RPCServerHandler(AtomicReference<Supplier<HandlerProcessor<T,Void>>> supplierAtomicReference) {
        this.supplierAtomicReference = supplierAtomicReference;
    }
    
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        log.info("客户端注册服务器"+ctx.channel().id());
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        log.info("客户端连接激活"+ctx.channel().id());
    
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        log.info("客户端断开连接"+ctx.channel().id());
    }
    
    @Override
    public void channelRead0(ChannelHandlerContext ctx, RPCContext t) {
        log.info("服务端接受处理 -> "+t);
        supplier.get().process((T)t.getTarget(), t);
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        log.error(cause.getMessage(),cause);
    }
}
