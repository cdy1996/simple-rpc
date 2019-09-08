package com.cdy.simplerpc.exception;

/**
 * 通用异常
 * Created by 陈东一
 * 2019/2/23 0023 21:43
 */
public class RPCRetryException extends RuntimeException {
    
    public RPCRetryException(Throwable cause) {
        super(cause);
    }
    
    public RPCRetryException() {
        super();
    }
    
    public RPCRetryException(String message) {
        super(message);
    }
    
    public RPCRetryException(String message, Throwable cause) {
        super(message, cause);
    }
    
    protected RPCRetryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
