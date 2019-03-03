package com.cdy.simplerpc;

import com.cdy.simplerpc.remoting.jetty.httpClient.HttpClientUtil;
import org.junit.Test;

/**
 * todo
 * Created by 陈东一
 * 2019/3/3 0003 17:27
 */
public class HttpClientTest {
    
    
    @Test
    public void test() throws Exception {
        String execute = HttpClientUtil.execute(HttpClientUtil.getHttpClient(), "http://localhost:80/gradle/count", null, 1000);
        System.out.println(execute);
    }
}
