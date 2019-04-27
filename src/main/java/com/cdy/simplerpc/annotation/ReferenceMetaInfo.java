package com.cdy.simplerpc.annotation;

import lombok.Data;

/**
 * todo
 * Created by 陈东一
 * 2019/3/3 0003 21:04
 */
@Data
public class ReferenceMetaInfo {
    
    public static final String METAINFO_KEY = "metaInfoKey";
    
    private boolean async = false;
    
    private long timeout = 5000L;
    
    private String[] protocols;
    
    public ReferenceMetaInfo(RPCReference annotation) {
        ReferenceMetaInfo referenceMetaInfo = new ReferenceMetaInfo();
        referenceMetaInfo.setAsync(annotation.async());
        referenceMetaInfo.setTimeout(annotation.timeout());
        referenceMetaInfo.setProtocols(annotation.protocols());
    }
    
    
    private ReferenceMetaInfo() {
    }
}
