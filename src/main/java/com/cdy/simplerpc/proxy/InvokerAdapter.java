package com.cdy.simplerpc.proxy;

/**
 * 客户端适配器
 *
 * Created by 陈东一
 * 2019/1/26 0026 21:53
 */
public abstract class InvokerAdapter implements Invoker{
    
    Invoker<?> invoker;
    
    public InvokerAdapter(Invoker<?> invoker) {
        this.invoker = invoker;
    }
    
    @Override
    public Object invoke(Invocation invocation) throws Exception {
        return invoker.invoke(invocation);
    }
    
    public Invoker<?> getInvoker() {
        return invoker;
    }
    
    public void setInvoker(Invoker<?> invoker) {
        this.invoker = invoker;
    }
}
