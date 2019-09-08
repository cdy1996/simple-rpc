package com.cdy.simplerpc.cluster;

import com.cdy.simplerpc.config.PropertySources;
import com.cdy.simplerpc.exception.RPCException;
import com.cdy.simplerpc.exception.RPCRetryException;
import com.cdy.simplerpc.netty.rpc.RPCContext;
import com.cdy.simplerpc.proxy.Invocation;
import com.cdy.simplerpc.registry.IServiceDiscovery;
import com.cdy.simplerpc.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 集群的装饰层
 * 快速恢复 记录失败请求, 定时重发, 消息类的通知
 * <p>
 * Created by 陈东一
 * 2019/4/27 0027 16:16
 */
@Slf4j
public class FailCacheClusterClient extends ClusterClient {
    
    ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    
    public FailCacheClusterClient(PropertySources propertySources, Map<String, IServiceDiscovery> servceDiscoveryMap) {
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
            try {
                Object result = getClient(serviceName, protocol).invoke(invocation);
                if (result instanceof Exception) {
                    log.error(((Exception) result).getMessage(), ((Exception) result));
                    return executorService.schedule(retry(serviceName, invocation, protocol),5, TimeUnit.SECONDS);
                }
                return result;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return executorService.schedule(retry(serviceName, invocation, protocol),5, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            throw new RPCRetryException(e);
        }
    }
    
    private Callable<Object> retry(String serviceName, Invocation invocation, String protocol) {
        return () -> {
            try {
                return getClient(serviceName, protocol).invoke(invocation);
            } catch (Exception e) {
                log.error("failcache retry error", e);
                throw new RPCException(e);
            }
        };
    }
}
