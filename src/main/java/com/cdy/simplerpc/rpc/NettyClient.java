package com.cdy.simplerpc.rpc;

import com.cdy.simplerpc.serialize.ISerialize;
import com.cdy.simplerpc.serialize.JdkSerialize;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.cdy.simplerpc.util.StringUtil.getServer;
import static com.cdy.simplerpc.util.StringUtil.toSocketAddress;

/**
 * 客户端
 * Created by 陈东一
 * 2018/11/25 0025 14:28
 */
@Slf4j
public class NettyClient<T, R> {
    
    /**
     * 上下文传递服务端address的key
     */
    public static final AttributeKey<String> ATTRIBUTE_KEY_ADDRESS = AttributeKey.valueOf("address");
    private final Bootstrap bootstrap;
    private final EventLoopGroup boss = new NioEventLoopGroup();
    private final AtomicInteger requestId = new AtomicInteger(0);
    //服务端地址
    private final Map<String, RPCFuture<R>> responseFuture = new ConcurrentHashMap<>();
    private volatile Channel channel;
    
    @Setter
    ISerialize serialize = new JdkSerialize();
    
    private NettyClient() {
        
        this.bootstrap = new Bootstrap();
        this.bootstrap.group(boss)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ByteBuf delimiter = Unpooled.copiedBuffer("@@@".getBytes());
                        ch.pipeline()
                        // 创建分隔符缓冲对象
                                .addLast(new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, delimiter/*SerializeCoderFactory.buffer*/))
//                                .addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4))
//                                .addLast(new LengthFieldPrepender(4))
//                                .addLast("encoder", new ObjectEncoder())
//                                .addLast("dencoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)))
                                .addLast("serialize-encoder", new SerializeEncoderHandler<>(serialize, RPCPackage.class))
                                .addLast("encoder", new RPCPackageEncoder())
                                .addLast("serialize-dencoder", new SerializeDecoderHandler<>(serialize, RPCPackage.class))
                                .addLast("dencoder", new RPCPackageClientDecoder())
                                .addLast(new SimpleChannelInboundHandler<RPCContext>() {
                                    @Override
                                    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                        Channel channel = ctx.channel();
                                        Attribute<String> attr = channel.attr(ATTRIBUTE_KEY_ADDRESS);
                                        String address = attr.get();
                                        log.warn("与服务端断开连接,尝试进行重新连接");
                                        super.channelInactive(ctx);
//                                        addressChannel.remove(address);
                                        //重新连接
                                        connect(NettyClient.this, address);
                                    }
                                    
                                    @Override
                                    protected void channelRead0(ChannelHandlerContext ctx, RPCContext msg) throws Exception {
                                        // 接收响应消息
                                        RPCFuture<R> rpcFuture = responseFuture.remove(msg.getRequestId());
                                        if (rpcFuture == null) {
                                            return;
                                        }
                                        rpcFuture.setAttach(msg.getAttach());
                                        rpcFuture.complete((R) msg.getTarget());
                                    }
                                });
                    }
                })
//                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true);
//                                    .childOption(ChannelOption.SO_KEEPALIVE, true);
    }
   
    
    public CompletableFuture<R> invokeAsync(T t) throws Exception {
    
        if (!channel.isActive()) {
            log.info("发送时检测channel失效了");
            channel.close();
            Attribute<String> attr = channel.attr(ATTRIBUTE_KEY_ADDRESS);
            String address = attr.get();
            connect( this, address);
        }
        
        int requestId = this.requestId.incrementAndGet();
        RPCFuture<R> future = new RPCFuture<>();
        responseFuture.put(requestId + "", future);
        
        // 因为可能 是服务端在发起的一次请求, 所以上下文的map继续带上
        RPCContext current = RPCContext.current();
        current.setRequestId(requestId + "");
        current.setCtx(null);
        current.setTarget(t);
        send(current, channel);
        
        return future;
    }
    
    public R invokeSync(T t) throws Exception {
        
        return invokeAsync(t).get();
    }
    
    public R invokeSync(T t, Long timeout, TimeUnit timeUnit) throws Exception {
        
        return invokeAsync(t).get(timeout, timeUnit);
    }
    
    private void send(RPCContext context, Channel channel) {
        ChannelFuture channelFuture = channel.writeAndFlush(context);
        channelFuture.addListener(future ->
                log.info("客户端发送完成 -> {}", context));
    }
    
    public static <T, R> NettyClient<T, R> connect(String address) throws Exception {
        NettyClient<T, R> nettyClient = new NettyClient<>();
        nettyClient.connect(nettyClient, address);
        return nettyClient;
    }
    
    
    private synchronized void connect(NettyClient nettyClient, String address) throws InterruptedException {
        ChannelFuture connect = nettyClient.bootstrap.connect(toSocketAddress(getServer(address)));
        ChannelFuture sync = connect.sync();
        nettyClient.channel = sync.channel();
        Attribute<String> attr = nettyClient.channel.attr(ATTRIBUTE_KEY_ADDRESS);
        attr.set(address);
        log.info("成功连接远程服务 -> "+address);
    
    }
    
    public void close() {
        boss.shutdownGracefully();
    }
    
}
