package com.cdy.simplerpc.balance;

import java.util.List;

/**
 * 负载均衡
 * Created by 陈东一
 * 2019/2/7 0007 20:09
 */
public interface IBalance {

    
    /**
     * 负载均衡 获取一个可用服务
     * @param serviceName  服务名称
     * @param list  服务实例列表
     * @return  一个实例
     */
    String loadBalance(String serviceName, List<String> list);
}
