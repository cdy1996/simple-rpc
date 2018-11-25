package com.cdy.simplerpc.remoting.netty;

import com.cdy.simplerpc.registry.IServiceDiscovery;
import com.cdy.simplerpc.remoting.Client;
import com.cdy.simplerpc.remoting.RPCRequest;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.lang.reflect.Method;

/**
 * 客户端
 * Created by 陈东一
 * 2018/11/25 0025 14:28
 */
public class RPCClient implements Client {
    
    IServiceDiscovery serviceDiscovery;
    
    public RPCClient(IServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }
    
    public Object invoke(Method method, Object[] args, Class interfaceClass) {
        RPCRequest rpcRequest = new RPCRequest();
        rpcRequest.setClassName(method.getDeclaringClass().getName());
        rpcRequest.setMethodName(method.getName());
        rpcRequest.setTypes(method.getParameterTypes());
        rpcRequest.setParams(args);
        
        //服务发现
        String serviceName = interfaceClass.getName();
        String address = serviceDiscovery.discovery(serviceName);
        
        //netty 连接
        EventLoopGroup boss = new NioEventLoopGroup();
        
        RPCProxyHandler rpcProxyHandler = new RPCProxyHandler();
        try {
            // 监听端口 并通讯
            
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(boss)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4))
                                    .addLast(new LengthFieldPrepender(4))
                                    .addLast("encoder", new ObjectEncoder())
                                    .addLast("dencoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)))
                                    .addLast(rpcProxyHandler);
                            
                        }
                    })
                    .option(ChannelOption.TCP_NODELAY, true);
//                                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            
            String[] addres = address.split(":");
            String ip = addres[0];
            int port = Integer.parseInt(addres[1]);
            
            ChannelFuture sync = bootstrap.connect(ip, port).sync();
            sync.channel().writeAndFlush(rpcRequest).sync();
            sync.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully();
        }
        return rpcProxyHandler.getResponse();
    }
    
}
