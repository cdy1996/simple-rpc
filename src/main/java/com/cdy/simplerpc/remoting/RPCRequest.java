package com.cdy.simplerpc.remoting;

import com.cdy.simplerpc.proxy.Arg;
import com.cdy.simplerpc.proxy.Invocation;
import com.cdy.simplerpc.serialize.ISerialize;
import com.cdy.simplerpc.serialize.JdkSerialize;
import lombok.Data;

import java.beans.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 传输对象
 * Created by 陈东一
 * 2018/9/1 22:15
 */
@Data
public class RPCRequest  implements Serializable {
    
    private static final long serialVersionUID = -7860988372110599223L;
    private String className;
    private String methodName;
    private Class<?>[] types;
    private Object[] params;


    // 只用于http模块
//    private Map<String, Object> attach = new HashMap<>();

    private Boolean generic;
    private Arg[] argsByte;

    private static ISerialize serialize = new JdkSerialize();

    public Invocation toInvocation() throws Exception {
        Invocation invocation ;
        if (getGeneric()){
            Arg[] argsByte = getArgsByte();
            List<Class<?>> types = new ArrayList<>();
            List<Object> params = new ArrayList<>();
            for (Arg arg : argsByte) {
                Class<?> clazz = Class.forName(arg.getType());
                types.add(clazz);
                params.add(serialize.deserialize(arg.getBytes(), clazz));
            }
            invocation = new Invocation(getMethodName(), params.toArray(), types.toArray(new Class[0]));
        } else {
            invocation = new Invocation(getMethodName(), getParams(), getTypes());
        }
        return invocation;
    }



}
