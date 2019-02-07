package com.cdy.simplerpc.balance;

import java.util.List;

/**
 * 简单负载均衡
 * Created by 陈东一
 * 2019/2/7 0007 20:13
 */
public class SimpleBalance implements IBalance{
    
    @Override
    public String loadBalance(List<String> list) {
        System.out.println("可用地址"+list);
        if (list == null || list.isEmpty()) {
            throw new RuntimeException("没有可用的地址");
        }
        if (list.size() == 1) {
            return list.get(0);
        } else {
            double v = Math.random() * list.size();
            return list.get((int) v);
        }
    }
}
