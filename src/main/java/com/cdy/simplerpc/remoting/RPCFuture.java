package com.cdy.simplerpc.remoting;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 传输对象
 * Created by 陈东一
 * 2018/9/1 22:15
 */
public class RPCFuture implements Serializable, Future<Object> {
    
    private static final long serialVersionUID = -3577840344928082441L;
    
    private Object resultData;
    private Map<String, Object> attach = new HashMap<>();
    private final Object lock;
    
    public RPCFuture() {
        this.lock = new Object();
    }
    
    public Object getResultData() {
        return resultData;
    }
    
    public void setResultData(Object resultData) {
        synchronized (lock){
            this.resultData = resultData;
            lock.notifyAll();
        }
    }
    
    public Map<String, Object> getAttach() {
        return attach;
    }
    
    public void setAttach(Map<String, Object> attach) {
        this.attach = attach;
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
    public Object get() throws InterruptedException, ExecutionException {
        synchronized (lock){
            if (resultData == null) {
                lock.wait();
            }
            return resultData;
        }
    }
    
    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        synchronized (lock){
            if (resultData == null) {
                lock.wait(timeout);
            }
            return resultData;
        }
    }
}
