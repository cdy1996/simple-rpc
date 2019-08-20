package com.cdy.simplerpc.rpc;

import com.cdy.simplerpc.remoting.rpc.RPCPackage;
import com.cdy.simplerpc.remoting.rpc.SerializeCoderFactory;
import com.cdy.simplerpc.remoting.rpc.SerializeDecoderHandler;
import com.cdy.simplerpc.remoting.rpc.SerializeEncoderHandler;
import com.cdy.simplerpc.serialize.ISerialize;
import com.cdy.simplerpc.serialize.JdkSerialize;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.AttributeKey;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.function.Supplier;

/**
 * 服务端
 * Created by 陈东一
 * 2018/9/1 21:41
 */
@Slf4j
public class RPCServer<T> {
    
    private Channel channel;
    private final EventLoopGroup boss = new NioEventLoopGroup();
    private final EventLoopGroup work = new NioEventLoopGroup();
    @Setter
    private ISerialize serialize = new JdkSerialize();
    private HandlerProcessor<T> handlerProcessor = (t, ctx) -> {
    };
    private final AttributeKey<Long> requestId = AttributeKey.valueOf("requestId");
    private Supplier<HandlerProcessor<T>> supplier = () -> handlerProcessor;
    
    
    public void addProcessor(HandlerProcessor<T> handlerProcessor) {
        this.supplier = () -> this.handlerProcessor.then(handlerProcessor);
    }
    
    public void openServer(String ip, Integer port) throws Exception {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boss, work)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, SerializeCoderFactory.buffer))
//                                .addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4))
//                                .addLast(new LengthFieldPrepender(4))
//                                .addLast("encoder", new ObjectEncoder())
//                                .addLast("dencoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)))
                                .addLast("encoder", new SerializeEncoderHandler<RPCPackage>(serialize, RPCPackage.class))
                                .addLast("encoder1", new MessageToMessageEncoder() {
                                    @Override
                                    protected void encode(ChannelHandlerContext ctx, Object msg, List out) throws Exception {
                                        out.add(new RPCPackage(ctx.channel().attr(requestId).get(), msg));
                                    }
                                })
                                .addLast("dencoder", new SerializeDecoderHandler<RPCPackage>(serialize, RPCPackage.class))
                                .addLast("dencoder1", new MessageToMessageDecoder<RPCPackage>() {
                                    @Override
                                    protected void decode(ChannelHandlerContext ctx, RPCPackage msg, List<Object> out) throws Exception {
                                        ctx.channel().attr(requestId).set(msg.getRequestId());
                                        out.add(msg.getTarget());
                                    }
                                })
                                .addLast(new RPCServerHandler<>(supplier));
                        
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        
        ChannelFuture channelFuture = bootstrap.bind(ip, port);
        channelFuture.syncUninterruptibly();
        this.channel = channelFuture.channel();
        
    }
    
    public void close() {
        try {
            channel.close();
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
        
        try {
            boss.shutdownGracefully();
            work.shutdownGracefully();
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }
    
}
