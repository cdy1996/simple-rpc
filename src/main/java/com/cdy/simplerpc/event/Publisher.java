package com.cdy.simplerpc.event;

import java.util.ArrayList;
import java.util.List;

/**
 * 事件发布
 * Created by 陈东一
 * 2019/4/14 0014 12:11
 */
public class Publisher {
    
    private List<RPCEventListener> listeners = new ArrayList<>();
    
    public synchronized void addListener(RPCEventListener eventListener) {
        listeners.add(eventListener);
    }
    
    
    public void publish(RPCEvent e) {
        listeners.forEach(listener -> listener.handle(e));
    }
    
    
}
