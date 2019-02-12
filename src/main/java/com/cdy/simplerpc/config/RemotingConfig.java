package com.cdy.simplerpc.config;

/**
 * 远程通信配置
 *
 * Created by 陈东一
 * 2018/11/26 0026 19:11
 */
public class RemotingConfig {
    
    // 通信类型 netty nio bio
    private RemotingType type;
    // 调用超时时间
    private Long invokeTimeout;
    // 熔断时间
    private Long breakTimeout;
    // 重试时间
    private Integer retryTime;
    
    
    public RemotingType getType() {
        return type;
    }
    
    public void setType(RemotingType type) {
        this.type = type;
    }
    
    public Long getInvokeTimeout() {
        return invokeTimeout;
    }
    
    public void setInvokeTimeout(Long invokeTimeout) {
        this.invokeTimeout = invokeTimeout;
    }
    
    public Long getBreakTimeout() {
        return breakTimeout;
    }
    
    public void setBreakTimeout(Long breakTimeout) {
        this.breakTimeout = breakTimeout;
    }
    
    public Integer getRetryTime() {
        return retryTime;
    }
    
    public void setRetryTime(Integer retryTime) {
        this.retryTime = retryTime;
    }
}
