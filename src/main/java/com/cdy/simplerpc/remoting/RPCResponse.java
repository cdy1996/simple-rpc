package com.cdy.simplerpc.remoting;

import java.io.Serializable;

/**
 * 传输对象
 * Created by 陈东一
 * 2018/9/1 22:15
 */
public class RPCResponse implements Serializable{
    
    
    private static final long serialVersionUID = -2916693541845750525L;
    
    private String requestId;
    private Object resultData;
    
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
