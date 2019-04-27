package com.cdy.simplerpc.event;

import java.util.EventListener;

/**
 * 事件监听接口
 * Created by 陈东一
 * 2019/4/14 0014 12:11
 */
public interface RPCEventListener extends EventListener {

    void handle(RPCEvent eventObject);
}
