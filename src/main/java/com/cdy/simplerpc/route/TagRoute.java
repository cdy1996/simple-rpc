package com.cdy.simplerpc.route;

import com.cdy.simplerpc.config.PropertySources;
import com.cdy.simplerpc.rpc.RPCContext;
import com.cdy.simplerpc.util.StringUtil;
import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 路由
 *
 * 需要开启服务名称的配置暴露 todo ,不然无法通过
 * Created by 陈东一
 * 2019/5/26 0026 10:34
 */
public class TagRoute implements IRoute {

    PropertySources propertySources;

    public TagRoute(PropertySources propertySources) {
        this.propertySources = propertySources;
    }


    /**
     * 筛选服务地址
     *
     * @param list 服务实例列表
     */
    @Override
    public List<String> route(List<String> list) {
//        RPCContext current = RPCContext.current();
//        List<String> tags = (List<String>) current.getAttach().get("tags");

        String clientTags = propertySources.resolveProperty(propertySources.resolveProperty("client") + ".tags", null);
        if (StringUtil.isBlank(clientTags)) {
            return list;
        }
        HashSet<String> strings = Sets.newHashSet(clientTags.split(","));
        int tags = strings.size();
        List<String> newList = new ArrayList<>();
        for (String address : list) {
            String serverTags = propertySources.resolveProperty(address + ".tags", null);
            if (StringUtil.isBlank(serverTags)) {
                newList.add(address);
            } else {
                HashSet<String> tagset = Sets.newHashSet(serverTags.split(","));
                if (Sets.union(strings, tagset).size()<(tags+ tagset.size())) {
                    //说明至少有一个标签完全一致,那么放行
                    newList.add(address);
                }
            }
        }

        return newList;




    }
}
