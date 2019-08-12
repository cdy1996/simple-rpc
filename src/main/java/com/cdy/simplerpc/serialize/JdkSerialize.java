package com.cdy.simplerpc.serialize;

import com.cdy.simplerpc.exception.SerializeException;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;

/**
 * jdk默认序列化实现
 * Created by 陈东一
 * 2019/8/12 0012 17:01
 */
public class JdkSerialize implements ISerialize<byte[]> {
    
    
    @Override
    public <IN>byte[] serialize(IN in, Class<IN> inClass){
        if (in instanceof Serializable) {
            Serializable serializable = (Serializable) in;
            return SerializationUtils.serialize(serializable);
        }
        throw new SerializeException(inClass.getName()+"必须实现序列化接口");
    }
    
    @Override
    public <IN>IN deserialize(byte[] out, Class<IN> inClass){
        try {
            return SerializationUtils.deserialize(out);
        } catch (Exception e) {
            throw new SerializeException(e);
        }
    }
    
 
}
