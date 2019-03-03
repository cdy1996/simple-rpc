package com.cdy.simplerpc.registry.simple;

import com.cdy.simplerpc.exception.DiscoveryException;
import com.cdy.simplerpc.registry.AbstractDiscovery;
import com.cdy.simplerpc.util.StringUtil;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cdy.simplerpc.util.StringUtil.splitWith;

/**
 * 简单发现
 * Created by 陈东一
 * 2019/1/22 0022 21:57
 */
public class SimpleDiscoveryImpl extends AbstractDiscovery {
    
    private Map<String, String> map = new HashMap<>();
    
    public SimpleDiscoveryImpl() {
        File file = new File("/simpleRPC-register-center.txt");
        if (file.exists()) {
            try {
                List<String> strings = FileUtils.readLines(file, "utf-8");
                strings.forEach(e->{
                    StringUtil.TwoResult<String, String> two = splitWith(e, " ");
                    map.put(two.getFirst(), two.getSecond());
                });
            } catch (IOException e) {
                throw new DiscoveryException(e);
            }
        }
    }
    
    @Override
    public String discovery(String serviceName) {
        String address = map.get(serviceName);
        return address;
    }
    
    @Override
    public List<String> listServer(String serviceName) throws Exception {
        String address = map.get(serviceName);
        return Collections.singletonList(address);
    }
}
