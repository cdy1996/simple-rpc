package com.cdy.simplerpc.remoting;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 传输对象
 * Created by 陈东一
 * 2018/9/1 22:15
 */
@Data
public class RPCRequest  implements Serializable {
    
    private static final long serialVersionUID = -7860988372110599223L;
    private String requestId;
    private String className;
    private String methodName;
    private Class<?>[] types;
    private Object[] params;
    private Map<String, Object> attach = new HashMap<>();
    
}
