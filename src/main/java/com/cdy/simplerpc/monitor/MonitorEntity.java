package com.cdy.simplerpc.monitor;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.Date;

/**
 * 监控传输对象
 * Created by 陈东一
 * 2019/2/8 0008 18:20
 */
public class MonitorEntity {
    
    public static ThreadLocal<MonitorEntity> local = new ThreadLocal<>();
    
    private String address;
    private Date startTime;
    private Long duraing;
    private String method;
    private String stack;
    private Object result;
    private Object[] args;
    
    public static void start(String address, Date date, String method, Object[] args){
        MonitorEntity monitorEntity = new MonitorEntity();
        monitorEntity.setAddress(address);
        monitorEntity.setStartTime(date);
        monitorEntity.setMethod(method);
        monitorEntity.setArgs(args);
        local.set(monitorEntity);
    }
    
    public static MonitorEntity end(Date end, Object result){
        try {
            MonitorEntity monitorEntity = local.get();
            monitorEntity.setDuraing(end.getTime()-monitorEntity.getStartTime().getTime());
            if (result instanceof Exception) {
                monitorEntity.setStack(ExceptionUtils.getStackTrace((Exception)result));
            } else {
                monitorEntity.setResult(result);
            }
            return monitorEntity;
        } finally {
            local.remove();
        }
    }
    
    
    public Object getResult() {
        return result;
    }
    
    public void setResult(Object result) {
        this.result = result;
    }
    
    public String getStack() {
        return stack;
    }
    
    public void setStack(String stack) {
        this.stack = stack;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public Date getStartTime() {
        return startTime;
    }
    
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
    
    public Long getDuraing() {
        return duraing;
    }
    
    public void setDuraing(Long duraing) {
        this.duraing = duraing;
    }
    
    public String getMethod() {
        return method;
    }
    
    public void setMethod(String method) {
        this.method = method;
    }
    
    public Object[] getArgs() {
        return args;
    }
    
    public void setArgs(Object[] args) {
        this.args = args;
    }
}
