package com.cdy.simplerpc.remoting.rpc;

import com.cdy.simplerpc.registry.IServiceRegistry;
import com.cdy.simplerpc.remoting.AbstractServer;
import com.cdy.simplerpc.remoting.ServerMetaInfo;
import com.cdy.simplerpc.util.StringUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static com.cdy.simplerpc.util.StringUtil.getServer;

/**
 * 服务端
 * Created by 陈东一
 * 2018/9/1 21:41
 */
@Slf4j
public class RPCServer extends AbstractServer {
    
    private Channel channel;
    private final EventLoopGroup boss = new NioEventLoopGroup();
    private final EventLoopGroup work = new NioEventLoopGroup();
    
    public RPCServer(ServerMetaInfo serverMetaInfo, List<IServiceRegistry> registry) {
        super(serverMetaInfo, registry);
    }
    
    @Override
    public void openServer() throws Exception {
        // 监听端口 并通讯
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boss, work)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4))
                                .addLast(new LengthFieldPrepender(4))
                                .addLast("encoder", new ObjectEncoder())
                                .addLast("dencoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)))
                                .addLast(new RPCServerHandler(getHandlerMap()));
                        
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        
        StringUtil.TwoResult<String, Integer> server = getServer(getAddress());
        String ip = server.getFirst();
        int port = server.getSecond();
        ChannelFuture channelFuture = bootstrap.bind(ip, port);
        channelFuture.syncUninterruptibly();
        this.channel = channelFuture.channel();
        
    }
    
    @Override
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
