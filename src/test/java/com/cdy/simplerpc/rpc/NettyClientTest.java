package com.cdy.simplerpc.rpc;

import org.junit.Test;

import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.*;

public class NettyClientTest {

    @Test
    public void invokeSync() throws Exception {
        NettyClient<String, String> client = new NettyClient<>();
        String s = client.invokeSync("!23");

    }

    @Test
    public void invokeAsync() throws Exception {
        NettyClient<String, String> client = new NettyClient<>();
        CompletableFuture<String> stringCompletableFuture = client.invokeAsync("!23");

    }
}