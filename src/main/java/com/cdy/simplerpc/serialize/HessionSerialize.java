package com.cdy.simplerpc.serialize;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.cdy.simplerpc.exception.SerializeException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * hession序列化实现
 * Created by 陈东一
 * 2019/8/12 0012 17:01
 */
public class HessionSerialize implements ISerialize<byte[]>{
    
    @Override
    public <IN>byte[] serialize(IN in, Class<IN> inClass){
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            HessianOutput hessianOutput = new HessianOutput(os);
            hessianOutput.writeObject(in);
            return os.toByteArray();
        } catch (IOException e) {
            throw new SerializeException(e);
        }
    }
    
    @Override
    public <IN>IN deserialize(byte[] out, Class<IN> inClass){
        HessianInput hessianInput = null;
        try ( ByteArrayInputStream is = new ByteArrayInputStream(out);){
            hessianInput = new HessianInput(is);
            return (IN) hessianInput.readObject();
        } catch (IOException e) {
            throw new SerializeException(e);
        } finally {
            if (hessianInput != null) {
                hessianInput.close();
            }
        }
    }
}
