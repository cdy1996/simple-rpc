package com.cdy.simplerpc.serialize;

import com.cdy.serialization.JsonUtil;
import com.cdy.simplerpc.exception.SerializeException;

import java.io.UnsupportedEncodingException;

/**
 * json序列化实现
 * Created by 陈东一
 * 2019/8/12 0012 17:01
 */
public class JsonSerialize implements ISerialize {
    
    @Override
    public <IN>byte[] serialize(IN in, Class<IN> inClass){
        try {
            return JsonUtil.toString(in).getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new SerializeException(e);
        }
    }
    
    @Override
    public <IN>IN deserialize(byte[] out, Class<IN> inClass){
        try {
            return JsonUtil.parseObject(new String(out, "utf-8"), inClass);
        } catch (UnsupportedEncodingException e) {
            throw new SerializeException(e);
        }
    }
}
