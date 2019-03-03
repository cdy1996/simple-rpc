package com.cdy.simplerpc.test;

import com.cdy.simplerpc.ClientBootStrap;
import com.cdy.simplerpc.annotation.RPCReference;
import com.cdy.simplerpc.balance.RibbonBalance;
import com.cdy.simplerpc.registry.zookeeper.ZKServiceDiscoveryImpl;
import com.cdy.simplerpc.remoting.netty.RPCClient;
import com.netflix.loadbalancer.RandomRule;

/**
 * 客户端测试
 * Created by 陈东一
 * 2019/1/22 0022 22:28
 */
public class ClientTest {
    
    @RPCReference
    private TestService testService;
    
    public void test() {
        testService.test("123");
    }
    
    public static void main(String[] args) throws Exception {
        ClientTest test = new ClientTest();
        
        ClientBootStrap clientBootStrap = ClientBootStrap.build()
                .balance(new RibbonBalance(new RandomRule()))
                .discovery(new ZKServiceDiscoveryImpl())
                .client(new RPCClient());
        
        ClientTest inject = (ClientTest) clientBootStrap.inject(test);
        inject.test();
        
        System.in.read();
        
    }
    
}
