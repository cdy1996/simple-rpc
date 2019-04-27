package com.cdy.simplerpc.monitor;

import lombok.Data;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.Date;
import java.util.concurrent.Future;

/**
 * 监控传输对象
 * Created by 陈东一
 * 2019/2/8 0008 18:20
 */
@Data
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
            } else if (result instanceof Future){
                monitorEntity.setResult("async invoke");
            } else {
                monitorEntity.setResult(result);
            }
            return monitorEntity;
        } finally {
            local.remove();
        }
    }
    
}
