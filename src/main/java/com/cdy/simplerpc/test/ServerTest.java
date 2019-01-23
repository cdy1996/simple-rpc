package com.cdy.simplerpc.test;

import com.cdy.simplerpc.ServerBootStrap;

import java.io.IOException;

/**
 * todo
 * Created by 陈东一
 * 2019/1/22 0022 22:28
 */
public class ServerTest {
    
    public static void main(String[] args) throws IOException {
        ServerBootStrap serverBootStrap = new ServerBootStrap();
        serverBootStrap.start(null, null);
    
        serverBootStrap.bind(new TestServiceImpl());
        System.in.read();
        
    }
    
}
