package com.cdy.simplerpc.proxy;

import com.cdy.simplerpc.remoting.RPCRequest;
import lombok.Data;

import java.lang.reflect.Method;

/**
 * 执行参数
 * Created by 陈东一
 * 2019/1/23 0023 23:03
 */
@Data
public class Invocation {
    
    /**
     * 客户端调用远程用的方法实例
     */
    private Method method;
    /**
     * 服务端调用本地时的方法名
     *
     * 泛化调用时客户端也会使用
     */
    private String methodName;
    /**
     * 客户端和服务端
     *         调用时的参数
     */
    private Object[] args;
    /**
     * 服务端调用本地时的参数类型
     */
    private Class<?>[] types;
    /**
     * 客户端调用远程时的接口名
     */
    private Class interfaceClass;


    private Boolean generic;

    private String interfaceClassName;

    private Arg[] argsByte;


    //用于构造代理对象
    public Invocation(Method method, Object[] args, Class interfaceClass) {
        this.method = method;
        this.args = args;
        this.interfaceClass = interfaceClass;
    }

    //客户端泛化对象
    public Invocation(String method, Arg[] args, String interfaceClass) {
        this.methodName = method;
        this.argsByte = args;
        this.interfaceClassName = interfaceClass;
    }
    
    //用于服务端的接受参数和调用
    public Invocation(String methodName, Object[] params, Class<?>[] types) {
        this.methodName = methodName;
        this.args = params;
        this.types = types;
    }
    
    public RPCRequest toRequest(){
        RPCRequest rpcRequest = new RPCRequest();
        if (getGeneric()){
            rpcRequest.setClassName(getInterfaceClassName());
            rpcRequest.setMethodName(getMethodName());
            rpcRequest.setArgsByte(getArgsByte());
        } else {
            rpcRequest.setClassName(getMethod().getDeclaringClass().getName());
            rpcRequest.setMethodName(getMethod().getName());
            rpcRequest.setTypes(getMethod().getParameterTypes());
            rpcRequest.setParams(getArgs());
        }
        return rpcRequest;
    }
    
}
