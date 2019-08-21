package com.cdy.simplerpc.remoting.http;

import com.cdy.simplerpc.remoting.ServerMetaInfo;
import com.cdy.simplerpc.serialize.JsonSerialize;
import org.junit.Test;

public class HttpServerTest {
    
    @Test
    public void openServer() throws Exception {
        HttpServer http = new HttpServer(new ServerMetaInfo("http", "8080", "127.0.0.1"), null, new JsonSerialize(), null);
        http.openServer();
    
        System.in.read();
    }
}