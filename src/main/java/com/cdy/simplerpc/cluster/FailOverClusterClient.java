package com.cdy.simplerpc.cluster;

import com.cdy.simplerpc.config.PropertySources;
import com.cdy.simplerpc.exception.RPCException;
import com.cdy.simplerpc.exception.RPCRetryException;
import com.cdy.simplerpc.netty.rpc.RPCContext;
import com.cdy.simplerpc.proxy.Invocation;
import com.cdy.simplerpc.registry.IServiceDiscovery;
import com.cdy.simplerpc.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 集群的装饰层
 * todo
 * 失败切换 重试其他服务
 * 快速失败 只请求一次
 * 快速恢复 记录失败请求, 定时重发, 消息类的通知
 * 广播请求 想所有服务发出请求
 * <p>
 * Created by 陈东一
 * 2019/4/27 0027 16:16
 */
@Slf4j
public class FailOverClusterClient extends ClusterClient {
    
    public FailOverClusterClient(PropertySources propertySources, Map<String, IServiceDiscovery> servceDiscoveryMap) {
        super(propertySources, servceDiscoveryMap);
    }
    
    @Override
    protected Object doInvoke(String protocols, String serviceName, IServiceDiscovery discovery, Invocation invocation) {
        RPCContext context = RPCContext.current();
        Map<String, Object> map = context.getAttach();
        Set<String> cache = new HashSet<>();
        try {
            // todo 重试次数可配置
            int size = discovery.listServer(serviceName, protocols).size();
            do {
                String address = nextAddress(discovery, protocols, serviceName, cache, size);
                cache.add(address);
                String[] split = address.split("-");
                String protocol = split[0];
                map.put("address", address.replace(protocol + "-", ""));
                try {
                    Object result = getClient(serviceName, protocol).invoke(invocation);
                    if (result instanceof Exception) {
                        log.error(((Exception) result).getMessage(), ((Exception) result));
                        continue;
                    }
                    return result;
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            } while (size-- > 0);
        } catch (Exception e) {
            throw new RPCRetryException(e);
        }
        throw new RPCRetryException();
    }
    
    private String nextAddress(IServiceDiscovery discovery, String protocols, String serviceName, Set<String> cache, int size) throws Exception {
        String newAddress;
        do {
            newAddress = discovery.discovery(serviceName, protocols);
            if (StringUtil.isBlank(newAddress)) {
                throw new RPCException("没有可用的地址");
            }
        } while (!cache.contains(newAddress) && size-->0);
        return newAddress;
    }
}
