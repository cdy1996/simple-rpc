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
public class RPCResponse implements Serializable {
    
    private static final long serialVersionUID = -7439160687592749322L;
    private Object resultData;

    //只用于http模块
//    private Map<String, Object> attach = new HashMap<>();
    
  
}
