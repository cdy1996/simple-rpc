package com.cdy.simplerpc.route;

import com.cdy.simplerpc.rpc.RPCContext;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 路由
 * Created by 陈东一
 * 2019/5/26 0026 10:34
 */
public class TagRoute implements IRoute {
    
    /**
     * 筛选服务地址
     *
     * @param list 服务实例列表
     */
    @Override
    public List<String> route(List<String> list) {
        RPCContext current = RPCContext.current();
        List<String> tags = (List<String>) current.getAttach().get("tags");
        
        // 配置中心配置如下
        // 服务端地址需要a,b,c这些标签
        // rpc-127.0.0.1:8888 -> a,b,c
        // http-127.0.0.1:8080 -> a,b
        // http-127.0.0.1:80 -> a,b
        
        Map<String, String> ipTag = null;
        
        return list.stream().filter(e -> {
            String[] split = ipTag.get(e).split(",");
            for (String s : split) {
                if (tags.contains(s)) {
                    return true;
                }
            }
            return false;
            
        }).collect(Collectors.toList());
    }
}
