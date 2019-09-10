package com.cdy.simplerpc.remoting;

import lombok.Data;

/**
 *  服务元信息
 * Created by 陈东一
 * 2019/8/3 003 20:07
 */
@Data
public class ServerMetaInfo {
    private final String protocol;
    private final String port;
    private final String ip;
    private final String serviceName;

}
