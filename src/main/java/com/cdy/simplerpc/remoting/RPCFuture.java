package com.cdy.simplerpc.remoting;

import java.io.Serializable;
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
    
    private String requestId;
    private Object resultData;
    private Object lock = new Object();
    
    
    public String getRequestId() {
        return requestId;
    }
    
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    
    public Object getResultData() {
        return resultData;
    }
    
    public void setResultData(Object resultData) {
        synchronized (lock){
            this.resultData = resultData;
            notifyAll();
        }
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
                wait();
            }
            return resultData;
        }
    }
    
    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        throw new UnsupportedOperationException("超时获取操作");
    }
}
