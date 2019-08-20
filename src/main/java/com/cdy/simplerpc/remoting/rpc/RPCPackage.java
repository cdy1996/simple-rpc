package com.cdy.simplerpc.remoting.rpc;

import lombok.Data;

import java.io.Serializable;

/**
 * 传输对象
 * Created by 陈东一
 * 2018/9/1 22:15
 */
@Data
public class RPCPackage implements Serializable {
    
    private static final long serialVersionUID = 5849581362089890989L;
   
    private Long requestId;
    private Object target;
    
    public RPCPackage(Object target) {
        this.target = target;
    }
    
    public RPCPackage(Long requestId, Object target) {
        this.requestId = requestId;
        this.target = target;
    }
}
