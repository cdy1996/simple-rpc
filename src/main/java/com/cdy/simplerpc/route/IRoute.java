package com.cdy.simplerpc.route;

import java.util.List;

/**
 * 路由
 * Created by 陈东一
 * 2019/5/26 0026 10:34
 */
public interface IRoute {
    
    /**
     * 筛选服务地址
     * @param list  服务实例列表
     */
    List<String> route(List<String> list);
}
