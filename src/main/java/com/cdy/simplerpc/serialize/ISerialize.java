package com.cdy.simplerpc.serialize;

/**
 * 序列化接口
 * Created by 陈东一
 * 2019/8/12 0012 17:01
 */
public interface ISerialize {
    
    <IN>byte[] serialize(IN in, Class<IN> inClass);
    
    <IN>IN deserialize(byte[] out, Class<IN> inClass);
}
