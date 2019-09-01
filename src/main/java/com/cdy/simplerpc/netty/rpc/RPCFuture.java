package com.cdy.simplerpc.netty.rpc;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 传输对象
 * Created by 陈东一
 * 2018/9/1 22:15
 */
public class RPCFuture<T> extends CompletableFuture<T>  {
//    @Getter @Setter
//    private T resultData;
    @Getter @Setter
    private Map<String, Object> attach = new HashMap<>();
//    private final Object lock= new Object();
    
    public RPCFuture() {
    }

    
//    @Override
//    public boolean cancel(boolean mayInterruptIfRunning) {
//        throw new UnsupportedOperationException("暂不支持取消操作");
//    }
//
//    @Override
//    public boolean isCancelled() {
//        throw new UnsupportedOperationException("暂不支持取消操作");
//    }
//
//    @Override
//    public boolean isDone() {
//        return resultData != null;
//    }
//
//    @Override
//    public T get() throws InterruptedException {
//        synchronized (lock){
//            lock.wait(defaultTimeout);
//            if (resultData == null) {
//                throw new InvokeTimeOutException();
//            }
//            return resultData;
//        }
//    }
//
//    @Override
//    public T get(long timeout, TimeUnit unit) throws InterruptedException {
//        synchronized (lock){
//            if (resultData == null) {
//                lock.wait(unit.toSeconds(timeout));
//                throw new InvokeTimeOutException();
//            }
//            return resultData;
//        }
//    }
    
    

}
