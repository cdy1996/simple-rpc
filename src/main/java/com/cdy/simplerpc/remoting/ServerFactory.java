package com.cdy.simplerpc.remoting;

import com.cdy.simplerpc.exception.RPCException;
import com.cdy.simplerpc.remoting.http.HttpServer;
import com.cdy.simplerpc.remoting.rpc.RPCServer;

/**
 * todo
 * Created by 陈东一
 * 2019/5/25 0025 19:51
 */
public class ServerFactory {
    
    public static String ip = "127.0.0.1";
    
    public static Server createServer(String protocol, String port) {
        switch (protocol) {
            case "rpc":
                return new RPCServer("rpc" , port, ip);
            case "http":
                return new HttpServer("http" ,port, ip);
            default:
                throw new RPCException("没有合适的协议");
        }
    }
}
