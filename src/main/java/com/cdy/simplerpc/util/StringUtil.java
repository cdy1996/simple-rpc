package com.cdy.simplerpc.util;

import com.netflix.loadbalancer.Server;
import io.netty.util.internal.SocketUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;

/**
 * 字符串工具
 * Created by 陈东一
 * 2019/1/27 0027 14:44
 */
public class StringUtil {
    
    public static boolean isBlank(String string){
        return StringUtils.isBlank(string);
    }
    public static boolean isNotBlank(String string){
        return !isBlank(string);
    }
    
    
    /**
     * 输入流转字符串
     * @param inputStream
     */
    public static String inputStreamToString(InputStream inputStream) throws Exception {
        try (ByteArrayOutputStream swapStream = new ByteArrayOutputStream()) {
            byte[] bytes = new byte[1024];
            if (inputStream.read(bytes) != -1) {
                swapStream.write(bytes);
            }
            return swapStream.toString("UTF-8");
        } finally {
            inputStream.close();
        }
    }
    public static byte[] inputStreamToBytes(InputStream inputStream) throws Exception {
        try (ByteArrayOutputStream swapStream = new ByteArrayOutputStream()) {
            byte[] bytes = new byte[1024];
            if (inputStream.read(bytes) != -1) {
                swapStream.write(bytes);
            }
            return swapStream.toByteArray();
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
    
    public static ThreeResult<String, String, Integer> getServerWithSchema(String address) {
        int i = address.indexOf("-");
        String schema = address.substring(0, i);
        TwoResult<String, String> stringStringTwoResult = splitWith(address.substring(i+1), ":");
        return new ThreeResult<>(schema, stringStringTwoResult.getFirst(), Integer.valueOf(stringStringTwoResult.getSecond()));
    }
    
    /**
     * 转换为{@link Server}
     * @param server
     * @return
     */
    public static Server toServer(ThreeResult<String, String, Integer> server){
        return new Server(server.getFirst(), server.getSecond(), server.getThird());
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
    
    public static class ThreeResult<T, E, F> {
        private T first;
        private E second;
        private F third;
        
        public ThreeResult(T first, E second, F third) {
            this.first = first;
            this.second = second;
            this.third = third;
        }
        
        public T getFirst() {
            return first;
        }
        
        public E getSecond() {
            return second;
        }
    
        public F getThird() {
            return third;
        }
    }
    
}
