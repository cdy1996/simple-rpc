package com.cdy.simplerpc.event;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 事件发布
 * Created by 陈东一
 * 2019/4/14 0014 12:11
 */
public class Publisher {
    
    private List<RPCEventListener> listeners = new ArrayList<>();
    private Map<String, List<RPCEventListener>> listenersMap = new ConcurrentHashMap<>();
    
    public synchronized void registry(RPCEventListener eventListener) {
        listeners.add(eventListener);
        for (Method method : eventListener.getClass().getMethods()) {
            if (method.getName().contains("handle")){
                Class<?> type = method.getParameters()[0].getType();
                System.out.println(type.getSimpleName());
                List<RPCEventListener> rpcEventListeners = listenersMap.get(type.getSimpleName());
                if (rpcEventListeners == null) {
                    listenersMap.put(type.getSimpleName(), new ArrayList<>(Collections.singleton(eventListener)));
                } else {
                    rpcEventListeners.add(eventListener);
                }
                listenersMap.put(type.getSimpleName(), new ArrayList<>(Collections.singleton(eventListener)));
                break;
            }
        }
        // 泛型的方式获取 需要截取比较麻烦
//        for (Type genericInterface : eventListener.getClass().getGenericInterfaces()) {
//            System.out.println(genericInterface.getTypeName());
//            List<RPCEventListener> rpcEventListeners = listenersMap.get(genericInterface.getTypeName());
//            if (rpcEventListeners == null) {
//                listenersMap.put(genericInterface.getTypeName(), new ArrayList<>(Collections.singleton(eventListener)));
//            } else {
//                rpcEventListeners.add(eventListener);
//            }
//        }
    }
    
    
    public void publish(RPCEvent e) {
        listenersMap.get(RPCEvent.class.getSimpleName()).forEach(listener-> listener.handle(e));
        listenersMap.get(e.getClass().getSimpleName()).forEach(listener-> listener.handle(e));
    }
    
    
}
