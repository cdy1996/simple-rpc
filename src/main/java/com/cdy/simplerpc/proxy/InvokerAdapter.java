package com.cdy.simplerpc.proxy;

import com.cdy.simplerpc.annotation.ReferenceMetaInfo;
import lombok.Data;

/**
 * 客户端适配器
 *
 * Created by 陈东一
 * 2019/1/26 0026 21:53
 */
@Data
public abstract class InvokerAdapter implements Invoker{
    
    Invoker<?> invoker;
    
    public InvokerAdapter(Invoker<?> invoker) {
        this.invoker = invoker;
    }
    
    @Override
    public Object invoke(Invocation invocation) throws Exception {
        return invoker.invoke(invocation);
    }
    
    @Override
    public void addMetaInfo(String s, ReferenceMetaInfo data) {
        invoker.addMetaInfo(s, data);
    }
    
    @Override
    public ReferenceMetaInfo getMetaInfo(String s) {
        return invoker.getMetaInfo(s);
    }
}
