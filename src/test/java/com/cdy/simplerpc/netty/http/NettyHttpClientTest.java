package com.cdy.simplerpc.netty.http;

import org.junit.Test;

public class NettyHttpClientTest {
    
    @Test
    public void invokeSync() throws Exception {
        NettyHttpClient<String, String> client = NettyHttpClient.connect("127.0.0.1:8080");
        String s = client.invokeSync("!23");
        System.out.println(s);
        client.invokeSync("!23");
        
    }
    
}