package com.cdy.simplerpc.config;

/**
 * 配置的key常量
 *
 * Created by 陈东一
 * 2019/5/25 0025 16:08
 */
public class ConfigConstants {
    
    /* 客户端的属性 */
    public static final String timeout = "timeout";
    public static final String async = "async";
    //直连地址
    public static final String url = "url";
    //接受的协议 rpc/http
    public static final String protocols = "protocols";
    
    /* 客户单服务端都有的属性 */
    public static final String group = "group";
    public static final String version = "version";
    
}
