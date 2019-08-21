package com.cdy.simplerpc.route;

import com.cdy.simplerpc.config.PropertySources;
import com.cdy.simplerpc.util.StringUtil;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 路由
 *
 *
 * iproute.whitelist -> rpc-127.0.0.1:8080
 * iproute.blacklist ->  http-127.0.0.1:80
 *
 * Created by 陈东一
 * 2019/5/26 0026 10:34
 */
public class IPRoute implements IRoute{
    
    PropertySources propertySources;

    public IPRoute(PropertySources propertySources) {
        this.propertySources = propertySources;
    }

    /**
     * 筛选服务地址
     * @param list  服务实例列表
     */
    @Override
    public List<String> route(List<String> list){
        //从配置中心拉取有用的路由信息
        //自己的ip -> 可访问的ip
        String white = propertySources.resolveProperty("iproute.whitelist", "");
        String blacklist = propertySources.resolveProperty("iproute.blacklist", "");
        return list.stream().filter(e-> {
            if (StringUtil.isBlank(white)) {
                return true;
            }
            return white.contains(e);
        }).filter(e-> !blacklist.contains(e)).collect(Collectors.toList());
    }
}
