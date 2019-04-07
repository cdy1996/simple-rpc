package com.cdy.simplerpc.proxy;

import com.cdy.simplerpc.annotation.ReferenceMetaInfo;

/**
 * 执行器
 * Created by 陈东一
 * 2019/1/24 0024 23:27
 */
public interface Invoker<T> {
    
    /**
     * 执行
     * @param invocation
     * @return
     * @throws Exception
     */
    Object invoke(Invocation invocation) throws Exception;
    
    void addMetaInfo(String s, ReferenceMetaInfo data);
    
}
