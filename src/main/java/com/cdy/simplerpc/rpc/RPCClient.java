//package com.cdy.simplerpc.rpc;
//
//import com.cdy.simplerpc.remoting.RPCFuture;
//import com.cdy.simplerpc.remoting.RPCRequest;
//import com.cdy.simplerpc.remoting.RPCResponse;
//import com.cdy.simplerpc.remoting.rpc.SerializeCoderFactory;
//import com.cdy.simplerpc.remoting.rpc.SerializeDecoderHandler;
//import com.cdy.simplerpc.remoting.rpc.SerializeEncoderHandler;
//import com.cdy.simplerpc.serialize.ISerialize;
//import com.cdy.simplerpc.serialize.JdkSerialize;
//import io.netty.bootstrap.Bootstrap;
//import io.netty.channel.*;
//import io.netty.channel.nio.NioEventLoopGroup;
//import io.netty.channel.socket.SocketChannel;
//import io.netty.channel.socket.nio.NioSocketChannel;
//import io.netty.handler.codec.DelimiterBasedFrameDecoder;
//import lombok.Setter;
//
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.atomic.AtomicInteger;
//import java.util.function.Supplier;
//
//import static com.cdy.simplerpc.util.StringUtil.getServer;
//import static com.cdy.simplerpc.util.StringUtil.toSocketAddress;
//
///**
// * 客户端
// * Created by 陈东一
// * 2018/11/25 0025 14:28
// */
//public class RPCClient<T>{
//
//    private final Bootstrap bootstrap ;
//    private static final EventLoopGroup boss = new NioEventLoopGroup();
//    private final AtomicInteger requestId = new AtomicInteger(0);
//    //服务端地址
//    private final Map<String, Channel> addressChannel = new ConcurrentHashMap<>();
//    private final Map<String, RPCFuture> responseFuture = new ConcurrentHashMap<>();
//
//    Class<T> clazz;
//
//    @Setter
//    ISerialize serialize = new JdkSerialize();
//    private SerializeEncoderHandler encoder =
//            new SerializeEncoderHandler(serialize, clazz);
//    private SerializeDecoderHandler dencoder =
//            new SerializeDecoderHandler(serialize, clazz);
//    private HandlerProcessor<T> handlerProcessor = (t,ctx)->{};
//    private Supplier<HandlerProcessor<T>> supplier = ()-> handlerProcessor;
//
//
//
//    public void addProcessor(HandlerProcessor<T> handlerProcessor){
//        this.supplier =  ()-> this.handlerProcessor.then(handlerProcessor);
//    }
//
//    public RPCClient() {
//
//        this.bootstrap = new Bootstrap();
//        this.bootstrap.group(boss)
//                .channel(NioSocketChannel.class)
//                .handler(new ChannelInitializer<SocketChannel>() {
//                    @Override
//                    protected void initChannel(SocketChannel ch) throws Exception {
//                        ch.pipeline()
//                                .addLast(new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, SerializeCoderFactory.buffer))
////                                .addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4))
////                                .addLast(new LengthFieldPrepender(4))
////                                .addLast("encoder", new ObjectEncoder())
////                                .addLast("dencoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)))
//                                .addLast("encoder", encoder)
//                                .addLast("dencoder", dencoder)
//                                .addLast(new SimpleChannelInboundHandler<Object>{
//                                        @Override
//                                        public void channelRead0(ChannelHandlerContext ctx, Object) throws Exception {
//                                            log.info("接收到内容 {}" ,rpcresponse);
//                                            RPCFuture rpcFuture = responseFuture.remove(rpcresponse.getRequestId());
//                                            if(rpcFuture == null){
//                                                return;
//                                            }
//                                            rpcFuture.setAttach(rpcresponse.getAttach());
//                                            rpcFuture.complete(rpcresponse.getResultData());
//
//                                    });
//                                 });
//                    }
//                })
//                .option(ChannelOption.TCP_NODELAY, true);
////                                    .childOption(ChannelOption.SO_KEEPALIVE, true);
//    }
//
//    public Object invoke(T t) throws Exception {
//
//
//        RPCFuture<Object> future = new RPCFuture<>();
//        responseFuture.put(rpcRequest.getRequestId(), future);
//
//        Channel finalChannel = channel;
//        send(t, finalChannel);
//            future.whenComplete((response, exception)->{
//                // 隐式接受参数
//                context.setMap(future.getAttach());
//            });
//            return future;
//
//    }
//
//    private void send(RPCRequest rpcRequest, Channel channel) {
//        channel.writeAndFlush(rpcRequest);
//    }
//
//    private Channel connect(String ip, Integer port) throws Exception {
//        String address = ip + ":" + port;
//        ChannelFuture sync = bootstrap.connect(toSocketAddress(getServer(address))).sync();
//        Channel channel = sync.channel();
//        addressChannel.put(address, channel);
//        return channel;
//    }
//
//    public void close() {
//        boss.shutdownGracefully();
//    }
//
//}
