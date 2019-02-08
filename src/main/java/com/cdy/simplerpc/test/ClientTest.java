package com.cdy.simplerpc.test;

import com.cdy.simplerpc.ClientBootStrap;
import com.cdy.simplerpc.container.RPCReference;

import java.io.IOException;

/**
 * todo
 * Created by 陈东一
 * 2019/1/22 0022 22:28
 */
public class ClientTest {
    
    @RPCReference
    private TestService testService;
    
    public void test(){
        testService.test("123");
    }
    
    public static void main(String[] args) throws IOException {
        ClientTest test = new ClientTest();
    
        ClientBootStrap clientBootStrap = new ClientBootStrap();
        clientBootStrap.start(null, null);
    
        ClientTest inject = (ClientTest)clientBootStrap.inject(test);
        inject.test();
    
        System.in.read();
        
    }
    
}
