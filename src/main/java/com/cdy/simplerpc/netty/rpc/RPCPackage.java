package com.cdy.simplerpc.netty.rpc;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 传输对象
 * Created by 陈东一
 * 2018/9/1 22:15
 */
@Data
@AllArgsConstructor
public class RPCPackage implements Serializable {
    
    private static final long serialVersionUID = 5849581362089890989L;
   
    private String requestId;
    private Object target;
    private Map<String, Object> map;

}
