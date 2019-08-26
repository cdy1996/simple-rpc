package com.cdy.simplerpc.rpc;

import com.cdy.simplerpc.serialize.ISerialize;
import com.cdy.simplerpc.serialize.JdkSerialize;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

/**
 * 服务端
 * Created by 陈东一
 * 2018/9/1 21:41
 *
 *
 * @param <T> 服务端接收到的泛型
 */
@Slf4j
public class NettyServer<T,R> {
    
    private Channel channel;
    private final EventLoopGroup boss = new NioEventLoopGroup();
    private final EventLoopGroup work = new NioEventLoopGroup();
    @Setter
    private ISerialize serialize = new JdkSerialize();
    private HandlerProcessor<R,Void> handlerProcessor = (t, context) -> {
        context.setTarget(t);
        context.getCtx().writeAndFlush(t); return null;
    };
    private Supplier<HandlerProcessor<T,Void>> supplier = null;

    public void addProcessor(HandlerProcessor<T,R> handlerProcessor) {
        if (supplier!=null) {
            this.supplier = () -> this.handlerProcessor.andThen(handlerProcessor);
        }
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
                                .addLast("serialize-encoder", new SerializeEncoderHandler<>(serialize, RPCPackage.class))
                                .addLast("encoder", new RPCPackageEncoder())
                                .addLast("serialize-dencoder", new SerializeDecoderHandler<>(serialize, RPCPackage.class))
                                .addLast("dencoder", new RPCPackageServerDecoder())
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