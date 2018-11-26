package com.cdy.simplerpc.remoting.netty;

import com.cdy.simplerpc.container.RPCService;
import com.cdy.simplerpc.registry.IServiceRegistry;
import com.cdy.simplerpc.remoting.Server;
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

import java.util.HashMap;

/**
 * 服务端
 * Created by 陈东一
 * 2018/9/1 21:41
 */
public class RPCServer implements Server {
    
    //服务注册
    //监听
    IServiceRegistry registry;
    String address;
    
    private HashMap<String, Object> handlerMap = new HashMap<>();
    
    
    public RPCServer(IServiceRegistry registry, String address) {
        this.registry = registry;
        this.address = address;
    }
    
    
    @Override
    public void bind(Object... services) {
        for (Object service : services) {
            RPCService annotation = service.getClass().getAnnotation(RPCService.class);
            String serviceName = annotation.clazz().getName();
            handlerMap.put(serviceName, service);
        }
    }
    
    @Override
    public void registerAndListen(){
        // 注册服务
        for (String s : handlerMap.keySet()) {
            //注册服务和地址
            registry.register(s, address);
        }
        try {
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
                                    .addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4,0,4))
                                    .addLast(new LengthFieldPrepender(4))
                                    .addLast("encoder", new ObjectEncoder())
                                    .addLast("dencoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)))
                                    .addLast(new RPCHandler(handlerMap));
                    
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            
            String[] addres = address.split(":");
            String ip = addres[0];
            int port = Integer.parseInt(addres[1]);
            ChannelFuture sync = bootstrap.bind(ip, port).sync();
            sync.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    
    
    }
}
