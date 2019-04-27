package com.cdy.simplerpc.remoting.rpc;

import com.cdy.simplerpc.annotation.ReferenceMetaInfo;
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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static com.cdy.simplerpc.annotation.ReferenceMetaInfo.METAINFO_KEY;
import static com.cdy.simplerpc.proxy.RemoteInvoker.responseFuture;
import static com.cdy.simplerpc.util.StringUtil.getServer;
import static com.cdy.simplerpc.util.StringUtil.toSocketAddress;

/**
 * 客户端
 * Created by 陈东一
 * 2018/11/25 0025 14:28
 */
public class RPCClient extends AbstractClient {
    
    private final Bootstrap bootstrap = new Bootstrap();
    private final EventLoopGroup boss = new NioEventLoopGroup();
    private static final AtomicInteger requestId = new AtomicInteger(0);
    public static final AttributeKey<String> ATTRIBUTE_KEY_ADDRESS = AttributeKey.valueOf("address");
    public static final ConcurrentHashMap<String, Channel> addressChannel = new ConcurrentHashMap<>();
    
    
    private void init() {
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
                                .addLast(new RPCClientHandler());
                    }
                })
                .option(ChannelOption.TCP_NODELAY, true);
//                                    .childOption(ChannelOption.SO_KEEPALIVE, true);
    }
    
    public RPCClient() {
        init();
    }
    
    @Override
    public Object invoke(Invocation invocation) throws Exception {
        RPCRequest rpcRequest = invocation.toRequest();
        rpcRequest.setRequestId(requestId.getAndIncrement() + "");
        
        //netty 连接
        String address = invocation.getAddress();
        Channel channel = addressChannel.get(address);
        if (channel == null) {
            channel = connect(address);
            addressChannel.putIfAbsent(address, channel);
        }
    
        addressChannel.put(address, channel);
        // 隐式传递参数
        RPCContext rpcContext1 = RPCContext.current();
        rpcRequest.setAttach(rpcContext1.getMap());
        rpcRequest.getAttach().put("address", address);
        
        ReferenceMetaInfo referenceMetaInfo = (ReferenceMetaInfo) invocation.getAttach().get(METAINFO_KEY);
        RPCFuture future = new RPCFuture(referenceMetaInfo.getTimeout());
        responseFuture.put(rpcRequest.getRequestId(), future);
        
        if (referenceMetaInfo.isAsync()) {
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
