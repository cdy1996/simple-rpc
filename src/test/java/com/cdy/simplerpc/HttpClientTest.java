package com.cdy.simplerpc;

import com.cdy.simplerpc.remoting.ClusterClient;
import com.cdy.simplerpc.remoting.http.httpClient.HttpClientUtil;
import org.junit.Test;

/**
 * httpclient测试
 *
 * Created by 陈东一
 * 2019/3/3 0003 17:27
 */
public class HttpClientTest {
    
    
    @Test
    public void test() throws Exception {
        String execute = HttpClientUtil.execute(HttpClientUtil.getHttpClient(), "http://localhost:80/gradle/count", null, 1000);
        System.out.println(execute);
    }
    
    public static void main(String[] args) {
        System.out.println(ClusterClient.class.getName().replace(ClusterClient.class.getSimpleName(), ""));
    }
}
