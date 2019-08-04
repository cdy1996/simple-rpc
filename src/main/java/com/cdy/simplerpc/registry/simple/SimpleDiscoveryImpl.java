package com.cdy.simplerpc.registry.simple;

import com.cdy.simplerpc.balance.IBalance;
import com.cdy.simplerpc.exception.DiscoveryException;
import com.cdy.simplerpc.registry.AbstractDiscovery;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 简单发现
 * Created by 陈东一
 * 2019/1/22 0022 21:57
 */
public class SimpleDiscoveryImpl extends AbstractDiscovery {
    //注册信息存放在内存中
    private Map<String, List<String>> map = new HashMap<>();
    
    public SimpleDiscoveryImpl(IBalance balance) {
        super(balance);
        File file = new File("/simple-rpc");
    
        if (file.isDirectory()) {
            for (File listFile : Objects.requireNonNull(file.listFiles())) {
                String name = listFile.getName();
                String seriviceName = name.substring(0, name.length() - 4);
                if (name.endsWith(".rpc")) {
                    List<String> urls;
                    try {
                        urls = FileUtils.readLines(listFile, "utf-8");
                    } catch (IOException e) {
                        throw new DiscoveryException(e);
                    }
                    List<String> cacheUrls = map.get(seriviceName);
                    if (cacheUrls == null) {
                        cacheUrls = new ArrayList<>(urls);
                        map.put(seriviceName, cacheUrls);
                    } else {
                        cacheUrls.addAll(urls);
                    }
                }
            
            }
        }
    }
    
    @Override
    public String discovery(String serviceName, String ...protocols) throws Exception {
        return loadBalance(serviceName, listServer(serviceName, protocols));
    }
    
    @Override
    public List<String> listServer(String serviceName, String ...protocols) throws Exception {
        List<String> value = map.get(serviceName);
        if (!(protocols == null || protocols.length==0)) {
            value = value.stream().filter(e-> Arrays.stream(protocols).anyMatch(e::startsWith)).collect(Collectors.toList());
        }
        return  value;
    }
    
}
