package com.cdy.simplerpc.proxy;

/**
 * 泛化服务接口
 */
public interface GenericService {


    Object invoke(String methodName, String[] paramsTypes, Arg[] params);
}
