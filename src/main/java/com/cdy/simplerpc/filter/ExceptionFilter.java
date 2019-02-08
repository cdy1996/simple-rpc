package com.cdy.simplerpc.filter;

import com.cdy.simplerpc.container.Order;
import com.cdy.simplerpc.proxy.Invocation;

/**
 * 用于处理所有可能发生的异常
 * Created by 陈东一
 * 2019/2/8 0008 17:17
 */
@Order(-2)
public class ExceptionFilter extends FilterAdapter{
    
    
    @Override
    public Object doFilter(Invocation invocation) throws Exception {
        Object o = null;
        try {
            o = super.doFilter(invocation);
        } catch (Exception e) {
            e.printStackTrace();
            return e;
        }
        return o;
    }
    
}
