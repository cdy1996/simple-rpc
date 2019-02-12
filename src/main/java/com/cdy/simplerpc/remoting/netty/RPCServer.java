package com.cdy.simplerpc.remoting.netty;

import com.cdy.simplerpc.remoting.AbstractServer;
import com.cdy.simplerpc.util.StringUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import static com.cdy.simplerpc.util.StringUtil.getServer;

/**
 * 服务端
 * Created by 陈东一
 * 2018/9/1 21:41
 */
public class RPCServer extends AbstractServer {
    
    
    public RPCServer(String address) {
        super(address);
    }
    
    @Override
    public void registerAndListen() throws Exception {
        register();
        
        // 监听端口 并通讯
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup work = new NioEventLoopGroup();
        
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
                                .addLast(new RPCServerHandler());
                        
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
    
        StringUtil.TwoResult<String, Integer> server = getServer(getAddress());
        String ip = server.getFirst();
        int port = server.getSecond();
        ChannelFuture sync = bootstrap.bind(ip, port).sync();
        sync.channel().closeFuture().sync();
        
        
    }
}
