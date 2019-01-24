package com.cdy.simplerpc.remoting;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 传输对象
 * Created by 陈东一
 * 2018/9/1 22:15
 */
public class RPCResponse implements Serializable{
    
    
    private static final long serialVersionUID = -2916693541845750525L;
    
    private String requestId;
    private Object resultData;
    private Map<String, Object> attach = new HashMap<>();
    
    public Map<String, Object> getAttach() {
        return attach;
    }
    
    public void setAttach(Map<String, Object> attach) {
        this.attach = attach;
    }
    
    public String getRequestId() {
        return requestId;
    }
    
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    
    public Object getResultData() {
        return resultData;
    }
    
    public void setResultData(Object resultData) {
        this.resultData = resultData;
    }
}
