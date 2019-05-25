package com.cdy.simplerpc.exception;

/**
 * 服务发现异常
 * Created by 陈东一
 * 2019/2/23 0023 21:45
 */
public class DiscoveryException extends RPCException {
    
    public DiscoveryException(Throwable cause) {
        super(cause);
    }
    
    public DiscoveryException() {
        super();
    }
    
    public DiscoveryException(String message) {
        super(message);
    }
    
    public DiscoveryException(String message, Throwable cause) {
        super(message, cause);
    }
    
    protected DiscoveryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
