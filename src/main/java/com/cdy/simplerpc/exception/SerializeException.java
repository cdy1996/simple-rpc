package com.cdy.simplerpc.exception;

/**
 * 序列化异常异常
 * Created by 陈东一
 * 2019/2/23 0023 21:45
 */
public class SerializeException extends RPCException {
    
    public SerializeException(Throwable cause) {
        super(cause);
    }
    
    public SerializeException() {
        super();
    }
    
    public SerializeException(String message) {
        super(message);
    }
    
    public SerializeException(String message, Throwable cause) {
        super(message, cause);
    }
    
    protected SerializeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
