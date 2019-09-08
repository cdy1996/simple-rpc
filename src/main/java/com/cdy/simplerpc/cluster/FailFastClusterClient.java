package com.cdy.simplerpc.cluster;

import com.cdy.simplerpc.config.PropertySources;
import com.cdy.simplerpc.exception.RPCException;
import com.cdy.simplerpc.netty.rpc.RPCContext;
import com.cdy.simplerpc.proxy.Invocation;
import com.cdy.simplerpc.registry.IServiceDiscovery;
import com.cdy.simplerpc.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 集群的装饰层
 * 快速失败 只请求一次
 * <p>
 * Created by 陈东一
 * 2019/4/27 0027 16:16
 */
@Slf4j
public class FailFastClusterClient extends ClusterClient {
    
    public FailFastClusterClient(PropertySources propertySources, Map<String, IServiceDiscovery> servceDiscoveryMap) {
        super(propertySources, servceDiscoveryMap);
    }
    
    @Override
    protected Object doInvoke(String protocols, String serviceName, IServiceDiscovery discovery, Invocation invocation) {
        RPCContext context = RPCContext.current();
        Map<String, Object> map = context.getAttach();
        try {
            String address = discovery.discovery(serviceName, protocols);
            if (StringUtil.isBlank(address)) {
                throw new RPCException("没有可用的地址");
            }
            String[] split = address.split("-");
            String protocol = split[0];
            map.put("address", address.replace(protocol + "-", ""));
            return getClient(serviceName, protocol).invoke(invocation);
        } catch (Exception e) {
            throw new RPCException(e);
        }
    }
}
