package com.cdy.simplerpc.serialize;

import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.cdy.simplerpc.exception.SerializeException;

import java.io.IOException;

/**
 * protobuf 序列化实现
 * Created by 陈东一
 * 2019/8/12 0012 17:01
 */
public class ProtobufSerialize implements ISerialize  {
    
    @Override
    public <IN>byte[] serialize(IN in, Class<IN> inClass){
        Codec<IN> personCodec = ProtobufProxy.create(inClass, false);
        try {
            return  personCodec.encode(in);
        } catch (IOException e) {
            throw new SerializeException(e);
        }
    }
    
    @Override
    public <IN>IN deserialize(byte[] out, Class<IN> inClass){
        Codec<IN> personCodec = ProtobufProxy.create(inClass, false);
        try {
            return personCodec.decode(out);
        } catch (IOException e) {
            throw new  SerializeException(e);
        }
    }
}
