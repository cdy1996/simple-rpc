package com.cdy.simplerpc.remoting;

import com.cdy.simplerpc.annotation.ReferenceMetaInfo;
import com.cdy.simplerpc.exception.RPCException;
import com.cdy.simplerpc.proxy.Invocation;
import com.cdy.simplerpc.registry.IServiceDiscovery;
import com.cdy.simplerpc.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.cdy.simplerpc.annotation.ReferenceMetaInfo.METAINFO_KEY;

/**
 * 集群的装饰层
 *
 * Created by 陈东一
 * 2019/4/27 0027 16:16
 */
@Slf4j
public class ClusterClient extends AbstractClient {
    
    private IServiceDiscovery servceDiscovery;
    private static Map<String, Client> clientMap = new ConcurrentHashMap<>();
    
    public ClusterClient(IServiceDiscovery servceDiscovery) {
        this.servceDiscovery = servceDiscovery;
    }
    
    @Override
    public Object invoke(Invocation invocation) throws Exception {
        ReferenceMetaInfo referenceMetaInfo = (ReferenceMetaInfo) invocation.getAttach().get(METAINFO_KEY);
        String directAddress = referenceMetaInfo.getUrl();
        
        //服务发现
        String serviceName = invocation.getInterfaceClass().getName();
        String address;
        if (StringUtil.isBlank(directAddress)) {
            address = servceDiscovery.discovery(serviceName, referenceMetaInfo.getProtocols());
        } else {
            //直连
            address = referenceMetaInfo.getUrl();
        }
        String[] split = address.split("-");
        String protocol = split[0];
        Client client = clientMap.get(serviceName + protocol);
        if (client == null) {
            try {
                String replace = ClusterClient.class.getName().replace(ClusterClient.class.getSimpleName(), "");
                Class<?> clazz = Class.forName(replace + protocol.toLowerCase() + "." + protocol.toUpperCase() + "Client");
                client = (Client) clazz.getConstructor().newInstance();
            } catch (ClassNotFoundException e) {
                throw new RPCException("不支持的协议" + protocol);
            }
            clientMap.putIfAbsent(serviceName + protocol, client);
        }
        invocation.setAddress(address.replace(protocol + "-", ""));
        return client.invoke(invocation);
    }
    
    @Override
    public void close() {
    
    }
}
