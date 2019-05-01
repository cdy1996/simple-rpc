package com.cdy.simplerpc.annotation;

import lombok.Data;

/**
 * 元信息实体
 * Created by 陈东一
 * 2019/3/3 0003 21:04
 */
@Data
public class ReferenceMetaInfo {
    
    private boolean async = false;
    
    private long timeout = 5000L;
    
    private String url;
    
    public static ReferenceMetaInfo generateMetaInfo(RPCReference annotation) {
        ReferenceMetaInfo referenceMetaInfo = new ReferenceMetaInfo();
        referenceMetaInfo.setAsync(annotation.async());
        referenceMetaInfo.setTimeout(annotation.timeout());
        referenceMetaInfo.setUrl(annotation.url());
        return referenceMetaInfo;
    }
    
    
    private ReferenceMetaInfo() {
    
    }
}
