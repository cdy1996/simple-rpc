package com.cdy.simplerpc.annotation;

import lombok.Data;

/**
 * todo
 * Created by 陈东一
 * 2019/3/3 0003 21:04
 */
@Data
public class ReferenceMetaInfo {
    
    private boolean async = false;
    
    private long timeout = 5000L;
    
    public ReferenceMetaInfo(RPCReference annotation) {
    
    
    }
    
    
}
