package com.cdy.simplerpc.registry.simple;

import com.cdy.simplerpc.registry.IServiceRegistry;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

/**
 * 简单注册中心
 * Created by 陈东一
 * 2019/1/22 0022 22:03
 */
public class SimpleRegisteryImpl implements IServiceRegistry {
    
    @Override
    public void register(String name, String address) {
        File file = new File("/simpleRPC-register-center.txt");
        if (!file.exists()) {
            file.mkdir();
        }
        try {
            FileUtils.writeLines(file, "utf-8", Collections.singleton(name + ":" + address));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
