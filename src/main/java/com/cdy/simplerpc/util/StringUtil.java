package com.cdy.simplerpc.util;

import com.netflix.loadbalancer.Server;
import io.netty.util.internal.SocketUtils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;

/**
 * 字符串工具
 * Created by 陈东一
 * 2019/1/27 0027 14:44
 */
public class StringUtil {
    
    /**
     * 输入流转字符串
     * @param inputStream
     */
    public static String inputStreamToString(InputStream inputStream) throws Exception {
        try (ByteArrayOutputStream swapStream = new ByteArrayOutputStream();) {
            byte[] bytes = new byte[1024];
            if (inputStream.read(bytes) != -1) {
                swapStream.write(bytes);
            }
            return swapStream.toString("UTF-8");
        } finally {
            inputStream.close();
        }
    }
    
    /**
     * 分割字符串
     * @param string
     * @param split
     * @return
     */
    public static TwoResult<String, String> splitWith(String string, String split){
        String[] split1 = string.split(split);
        return new TwoResult<>(split1[0], split1[1]);
    }
    
    /**
     * 分割字符串获取host + port
     * @param address
     * @return
     */
    public static TwoResult<String, Integer> getServer(String address) {
        TwoResult<String, String> stringStringTwoResult = splitWith(address, ":");
        return new TwoResult<>(stringStringTwoResult.getFirst(), Integer.valueOf(stringStringTwoResult.getSecond()));
    }
    
    /**
     * 转换为{@link Server}
     * @param server
     * @return
     */
    public static Server toServer(TwoResult<String, Integer> server){
        return new Server(server.getFirst(), server.getSecond());
    }
    
    /**
     * 转换为{@link InetSocketAddress}
     * @param server
     * @return
     */
    public static InetSocketAddress toSocketAddress(TwoResult<String, Integer> server){
        return SocketUtils.socketAddress(server.getFirst(), server.getSecond());
    }
    
    public static class TwoResult<T, E> {
        private T first;
        private E second;
        
        public TwoResult(T first, E second) {
            this.first = first;
            this.second = second;
        }
        
        public T getFirst() {
            return first;
        }
        
        public E getSecond() {
            return second;
        }
    }
    
}
