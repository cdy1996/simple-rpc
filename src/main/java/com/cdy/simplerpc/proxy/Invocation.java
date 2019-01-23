package com.cdy.simplerpc.proxy;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 执行参数
 * Created by 陈东一
 * 2019/1/23 0023 23:03
 */
public class Invocation {
    
    private Method method;
    private Object[] args;
    private Class interfaceClass;
    private Map<String, Object> attach = new HashMap<>();
    
    public Invocation(Method method, Object[] args, Class interfaceClass) {
        this.method = method;
        this.args = args;
        this.interfaceClass = interfaceClass;
    }
    
    public Method getMethod() {
        return method;
    }
    
    public void setMethod(Method method) {
        this.method = method;
    }
    
    public Object[] getArgs() {
        return args;
    }
    
    public void setArgs(Object[] args) {
        this.args = args;
    }
    
    public Class getInterfaceClass() {
        return interfaceClass;
    }
    
    public void setInterfaceClass(Class interfaceClass) {
        this.interfaceClass = interfaceClass;
    }
    
    public Map<String, Object> getAttach() {
        return attach;
    }
    
    public void setAttach(Map<String, Object> attach) {
        this.attach = attach;
    }
}
