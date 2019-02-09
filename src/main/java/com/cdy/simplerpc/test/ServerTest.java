package com.cdy.simplerpc.test;

import com.cdy.simplerpc.ServerBootStrap;

/**
 * 服务端测试
 * Created by 陈东一
 * 2019/1/22 0022 22:28
 */
public class ServerTest {
    
    public static void main(String[] args) throws Exception {
        ServerBootStrap serverBootStrap = new ServerBootStrap();
        serverBootStrap.start(null, null);
    
        serverBootStrap.bind(new TestServiceImpl());
        System.in.read();
        
    }
    
}
