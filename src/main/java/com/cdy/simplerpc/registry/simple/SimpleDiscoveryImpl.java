package com.cdy.simplerpc.registry.simple;

import com.cdy.simplerpc.registry.IServiceDiscovery;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 简单发现
 * Created by 陈东一
 * 2019/1/22 0022 21:57
 */
public class SimpleDiscoveryImpl implements IServiceDiscovery {
    
    private Map<String, String> map = new HashMap<>();
    
    public SimpleDiscoveryImpl() {
        File file = new File("/simpleRPC-register-center.txt");
        if (file.exists()) {
            try {
                List<String> strings = FileUtils.readLines(file, "utf-8");
                strings.forEach(e->{
                    String[] split = e.split(" ");
                    map.put(split[0], split[1]);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public String discovery(String serviceName) {
        String address = map.get(serviceName);
        return address;
    }
}
