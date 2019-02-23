package com.cdy.simplerpc.exception;

/**
 * 通用异常
 * Created by 陈东一
 * 2019/2/23 0023 21:43
 */
public class RPCException extends RuntimeException {
    
    private static final long serialVersionUID = 1373538162299799580L;
    
    public RPCException(Throwable cause) {
        super(cause);
    }
    
    public RPCException() {
        super();
    }
    
    public RPCException(String message) {
        super(message);
    }
    
    public RPCException(String message, Throwable cause) {
        super(message, cause);
    }
    
    protected RPCException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
