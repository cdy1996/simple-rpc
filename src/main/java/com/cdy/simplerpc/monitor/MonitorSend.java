package com.cdy.simplerpc.monitor;

/**
 * 监控信息发送
 * Created by 陈东一
 * 2019/2/8 0008 18:39
 */
public interface MonitorSend {
    
    void send(MonitorEntity monitorEntity);
}
