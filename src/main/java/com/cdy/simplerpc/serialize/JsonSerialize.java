package com.cdy.simplerpc.serialize;

import com.cdy.simplerpc.exception.SerializeException;

/**
 * json序列化实现
 * Created by 陈东一
 * 2019/8/12 0012 17:01
 */
public class JsonSerialize implements ISerialize {
    
    @Override
    public <IN>byte[] serialize(IN in, Class<IN> inClass){
        try {
            return null;//JsonUtil.toString(in).getBytes(UTF8);
        } catch (Exception e) {
            throw new SerializeException(e);
        }
    }
    
    @Override
    public <IN>IN deserialize(byte[] out, Class<IN> inClass){
        try {
            return null;//JsonUtil.parseObject(new String(out, UTF8), inClass);
        } catch (Exception e) {
            throw new SerializeException(e);
        }
    }
}
