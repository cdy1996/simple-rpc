package com.cdy.simplerpc.netty.rpc;

import org.junit.Test;

import java.util.concurrent.CompletableFuture;

public class NettyClientTest {

    @Test
    public void invokeSync() throws Exception {
        NettyClient<String, String> client = NettyClient.connect("127.0.0.1:8080");
        String s = client.invokeSync("!23");
        System.out.println(s);
        client.invokeSync("!23");
        
    }

    @Test
    public void invokeAsync() throws Exception {
        NettyClient<String, String> client =  NettyClient.connect("127.0.0.1:8080");
        CompletableFuture<String> stringCompletableFuture = client.invokeAsync("!23");

    }
}