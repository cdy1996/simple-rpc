package com.cdy.simplerpc.serialize;

import com.cdy.serialization.JsonUtil;

/**
 * json序列化实现
 * Created by 陈东一
 * 2019/8/12 0012 17:01
 */
public class JsonSerialize implements ISerialize<String> {
    
    @Override
    public <IN>String serialize(IN in, Class<IN> inClass){
        return JsonUtil.toString(in);
    }
    
    @Override
    public <IN>IN deserialize(String out, Class<IN> inClass){
        return JsonUtil.parseObject(out, inClass);
    }
}
