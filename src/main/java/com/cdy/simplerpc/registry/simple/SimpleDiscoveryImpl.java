package com.cdy.simplerpc.registry.simple;

import com.cdy.simplerpc.exception.DiscoveryException;
import com.cdy.simplerpc.registry.AbstractDiscovery;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 简单发现
 * Created by 陈东一
 * 2019/1/22 0022 21:57
 */
public class SimpleDiscoveryImpl extends AbstractDiscovery {
    
    private Map<String, List<String>> map = new HashMap<>();
    
    public SimpleDiscoveryImpl() {
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
    public String discovery(String serviceName, String type) {
        List<String> urls = map.get(serviceName);
        urls.removeIf(e -> !e.startsWith(type));
        urls.replaceAll(e-> e.replace(type+"-",""));
        return loadBalance(serviceName, urls);
    }
    
    @Override
    public List<String> listServer(String serviceName) throws Exception {
        return map.get(serviceName) ;
    }
    
}
