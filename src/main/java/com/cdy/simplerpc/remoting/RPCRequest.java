package com.cdy.simplerpc.remoting;

import java.util.HashMap;
import java.util.Map;

/**
 * 传输对象
 * Created by 陈东一
 * 2018/9/1 22:15
 */
public class RPCRequest  {
    
    private String requestId;
    private String className;
    private String methodName;
    private Class<?>[] types;
    private Object[] params;
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
    
    public String getClassName() {
        return className;
    }
    
    public void setClassName(String className) {
        this.className = className;
    }
    
    public String getMethodName() {
        return methodName;
    }
    
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
    
    public Class<?>[] getTypes() {
        return types;
    }
    
    public void setTypes(Class<?>[] types) {
        this.types = types;
    }
    
    public Object[] getParams() {
        return params;
    }
    
    public void setParams(Object[] params) {
        this.params = params;
    }
}
