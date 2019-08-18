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
public class HessionSerialize implements ISerialize{
    
    @Override
    public <IN>byte[] serialize(IN in, Class<IN> inClass){
        HessianOutput hessianOutput = null;
        try ( ByteArrayOutputStream os = new ByteArrayOutputStream();){
           
            hessianOutput = new HessianOutput(os);
            hessianOutput.writeObject(in);
            return os.toByteArray();
        } catch (IOException e) {
            throw new SerializeException(e);
        } finally {
            if (hessianOutput!=null) {
                try {
                    hessianOutput.close();
                } catch (IOException ignored) {}
            }
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
