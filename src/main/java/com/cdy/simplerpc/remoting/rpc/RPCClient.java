package com.cdy.simplerpc.remoting.rpc;

import com.cdy.simplerpc.config.ConfigConstants;
import com.cdy.simplerpc.config.PropertySources;
import com.cdy.simplerpc.exception.RPCException;
import com.cdy.simplerpc.proxy.Invocation;
import com.cdy.simplerpc.remoting.AbstractClient;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static com.cdy.simplerpc.util.StringUtil.getServer;
import static com.cdy.simplerpc.util.StringUtil.toSocketAddress;

/**
 * 客户端
 * Created by 陈东一
 * 2018/11/25 0025 14:28
 */
public class RPCClient extends AbstractClient {
    /**
     * 上下文传递服务端address的key
     */
    public static final AttributeKey<String> ATTRIBUTE_KEY_ADDRESS = AttributeKey.valueOf("address");
    
    private final Bootstrap bootstrap = new Bootstrap();
    private static final EventLoopGroup boss = new NioEventLoopGroup();
    private final AtomicInteger requestId = new AtomicInteger(0);
    //服务端地址
    private final Map<String, Channel> addressChannel = new ConcurrentHashMap<>();
    private final Map<String, RPCFuture> responseFuture = new ConcurrentHashMap<>();
    
    
    
    private void init() {
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
                                .addLast(new RPCClientHandler(addressChannel,responseFuture));
                    }
                })
                .option(ChannelOption.TCP_NODELAY, true);
//                                    .childOption(ChannelOption.SO_KEEPALIVE, true);
    }
    
    public RPCClient(PropertySources propertySources) {
        super(propertySources);
        init();
    }
    
    @Override
    public Object invoke(Invocation invocation) throws Exception {
     
        RPCContext context = RPCContext.current();
        Map<String, Object> contextMap = context.getMap();
    
        // 隐式传递参数
        RPCRequest rpcRequest = invocation.toRequest();
        rpcRequest.setRequestId(requestId.getAndIncrement() + "");
        rpcRequest.setAttach(contextMap);
        
        //netty 连接
        String address = (String) contextMap.get(RPCContext.address);
        Channel channel = addressChannel.get(address);
        if (channel == null) {
            channel = connect(address);
            addressChannel.putIfAbsent(address, channel);
        }
    
        addressChannel.put(address, channel);
    
        String annotationKey = (String) contextMap.get(RPCContext.annotationKey);
        String async = propertySources.resolveProperty(annotationKey + "." + ConfigConstants.async);
        String timeout = propertySources.resolveProperty(annotationKey + "." + ConfigConstants.timeout);
    
    
        RPCFuture future = new RPCFuture(Long.valueOf(timeout));
        responseFuture.put(rpcRequest.getRequestId(), future);
        
        if ("true".equalsIgnoreCase(async)) {
            Channel finalChannel = channel;
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return send(rpcRequest, finalChannel, future);
                } catch (Exception e) {
                    throw new RPCException(e);
                }
            });
        } else {
            return send(rpcRequest, channel, future);
        }
    }
    
    private Object send(RPCRequest rpcRequest, Channel channel, RPCFuture future) throws InterruptedException {
        channel.writeAndFlush(rpcRequest);
        Object result = future.get();
        // 隐式接受参数
        RPCContext rpcContext = RPCContext.current();
        rpcContext.setMap(future.getAttach());
        return result;
    }

    private Channel connect(String address) throws Exception {
        ChannelFuture sync = bootstrap.connect(toSocketAddress(getServer(address))).sync();
        Channel channel = sync.channel();
        Attribute<String> attr = channel.attr(ATTRIBUTE_KEY_ADDRESS);
        attr.set(address);
        addressChannel.put(address, channel);
        return channel;
    }
    
    @Override
    public void close() {
        boss.shutdownGracefully();
    }
    
}
