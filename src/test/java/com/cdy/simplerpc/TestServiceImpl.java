package com.cdy.simplerpc;

import com.cdy.simplerpc.annotation.RPCService;

/**
 * 测试
 * Created by 陈东一
 * 2019/1/22 0022 22:14
 */
@RPCService
public class TestServiceImpl implements TestService{
    
    public String test(String test){
        return test;
    }

}
