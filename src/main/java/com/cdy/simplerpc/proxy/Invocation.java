package com.cdy.simplerpc.proxy;

import com.cdy.simplerpc.remoting.RPCRequest;

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
    private String methodName;
    private Object[] args;
    private Class<?>[] types;
    private Class interfaceClass;
    private Map<String, Object> attach = new HashMap<>();
    
    public Map<String, Object> getAttach() {
        return attach;
    }
    
    public void setAttach(Map<String, Object> attach) {
        this.attach = attach;
    }
    
    //用于构造代理对象
    public Invocation(Method method, Object[] args, Class interfaceClass) {
        this.method = method;
        this.args = args;
        this.interfaceClass = interfaceClass;
    }
    
    //用于服务端的接受参数和调用
    public Invocation(String methodName, Object[] params, Class<?>[] types) {
        this.methodName = methodName;
        this.args = params;
        this.types = types;
    }
    
    public RPCRequest toRequest(){
        RPCRequest rpcRequest = new RPCRequest();
        rpcRequest.setClassName(getMethod().getDeclaringClass().getName());
        rpcRequest.setMethodName(getMethod().getName());
        rpcRequest.setTypes(getMethod().getParameterTypes());
        rpcRequest.setParams(getArgs());
        return rpcRequest;
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
    
    public Class<?>[] getTypes() {
        return types;
    }
    
    public void setTypes(Class<?>[] types) {
        this.types = types;
    }
    
    public String getMethodName() {
        return methodName;
    }
    
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
}