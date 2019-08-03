package com.cdy.simplerpc.remoting;

import com.cdy.simplerpc.exception.InvokeTimeOutException;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 传输对象
 * Created by 陈东一
 * 2018/9/1 22:15
 */
public class RPCFuture implements Future<Object> {
    @Getter @Setter
    private Object resultData;
    @Getter @Setter
    private Map<String, Object> attach = new HashMap<>();
    private final Object lock= new Object();
    private final long defaultTimeout ;
    
    public RPCFuture() {
        this.defaultTimeout = 5000L;
    }
    public RPCFuture(Long timeout) {
        this.defaultTimeout = timeout;
    }

    
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        throw new UnsupportedOperationException("暂不支持取消操作");
    }
    
    @Override
    public boolean isCancelled() {
        throw new UnsupportedOperationException("暂不支持取消操作");
    }
    
    @Override
    public boolean isDone() {
        return resultData != null;
    }
    
    @Override
    public Object get() throws InterruptedException {
        synchronized (lock){
            lock.wait(defaultTimeout);
            if (resultData == null) {
                throw new InvokeTimeOutException();
            }
            return resultData;
        }
    }
    
    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException {
        synchronized (lock){
            if (resultData == null) {
                lock.wait(unit.toSeconds(timeout));
                throw new InvokeTimeOutException();
            }
            return resultData;
        }
    }
    
    

}
