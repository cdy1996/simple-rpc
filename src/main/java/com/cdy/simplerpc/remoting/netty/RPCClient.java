package com.cdy.simplerpc.remoting.netty;

import com.cdy.simplerpc.proxy.Invocation;
import com.cdy.simplerpc.registry.IServiceDiscovery;
import com.cdy.simplerpc.remoting.Client;
import com.cdy.simplerpc.remoting.RPCContext;
import com.cdy.simplerpc.remoting.RPCFuture;
import com.cdy.simplerpc.remoting.RPCRequest;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import static com.cdy.simplerpc.remoting.netty.RPCProxyHandler.responseConcurrentHashMap;

/**
 * 客户端
 * Created by 陈东一
 * 2018/11/25 0025 14:28
 */
public class RPCClient implements Client {
    
    private IServiceDiscovery serviceDiscovery;
    private Bootstrap bootstrap;
    private EventLoopGroup boss;
    private AtomicInteger requestId = new AtomicInteger(0);
    public static final AttributeKey<String> ATTRIBUTE_KEY_ADDRESS = AttributeKey.valueOf("address");
    public static final AttributeKey<Map<String, Object>> ATTRIBUTE_KEY_ATTACH = AttributeKey.valueOf("attach");
    
    
    private ConcurrentHashMap<String, Channel> concurrentHashMap = new ConcurrentHashMap<>();
    
    private void init(){
        boss = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
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
                                .addLast(new RPCProxyHandler(concurrentHashMap));
                    }
                })
                .option(ChannelOption.TCP_NODELAY, true);
//                                    .childOption(ChannelOption.SO_KEEPALIVE, true);
    }
    
    public RPCClient(IServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
        init();
    }
    
    public Channel connect(String serviceName){
        try {
            String address = serviceDiscovery.discovery(serviceName);
            String[] addres = address.split(":");
            String ip = addres[0];
            int port = Integer.parseInt(addres[1]);
            ChannelFuture sync = bootstrap.connect(ip, port).sync();
            Channel channel = sync.channel();
            Attribute<String> attr = channel.attr(ATTRIBUTE_KEY_ADDRESS);
            attr.set(address);
            concurrentHashMap.put(address, channel);
            return channel;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public Object invoke(Invocation invocation) {
        RPCRequest rpcRequest = new RPCRequest();
        rpcRequest.setClassName(invocation.getMethod().getDeclaringClass().getName());
        rpcRequest.setMethodName(invocation.getMethod().getName());
        rpcRequest.setTypes(invocation.getMethod().getParameterTypes());
        rpcRequest.setParams(invocation.getArgs());
        rpcRequest.setRequestId(requestId.getAndIncrement()+"");
        //服务发现
        String serviceName = invocation.getInterfaceClass().getName();
        //netty 连接
        Channel channel = concurrentHashMap.getOrDefault(serviceName, connect(serviceName));
        concurrentHashMap.put(serviceName, channel);
        // 隐式传递参数
        Attribute<Map<String, Object>> attr = channel.attr(ATTRIBUTE_KEY_ATTACH);
        attr.set(invocation.getAttach());
        
        RPCFuture rpcResponse = new RPCFuture();
        responseConcurrentHashMap.put(rpcRequest.getRequestId(), rpcResponse);
    
        
        channel.writeAndFlush(rpcRequest);
        try {
            Object result = rpcResponse.get();
            // 隐式接受参数
            attr = channel.attr(ATTRIBUTE_KEY_ATTACH);
            RPCContext rpcContext = RPCContext.local.get();
            rpcContext.setMap(attr.get());
            return result;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    
    public void channelClose(String serviceName, Channel channel){
        channel.close();
        concurrentHashMap.remove(serviceName);
    }
    
    public void close(){
        boss.shutdownGracefully();
    }
    
    public IServiceDiscovery getServiceDiscovery() {
        return serviceDiscovery;
    }
    
    public ConcurrentHashMap<String, Channel> getConcurrentHashMap() {
        return concurrentHashMap;
    }
}
