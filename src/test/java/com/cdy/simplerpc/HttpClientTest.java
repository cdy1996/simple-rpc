package com.cdy.simplerpc;

import com.cdy.simplerpc.exception.RPCException;
import com.cdy.simplerpc.remoting.ClusterClient;
import com.cdy.simplerpc.remoting.http.HttpClientUtil;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.junit.Test;

import java.io.IOException;

/**
 * httpclient测试
 *
 * Created by 陈东一
 * 2019/3/3 0003 17:27
 */
public class HttpClientTest {
    
    
    @Test
    public void test() throws Exception {
        byte[] execute = HttpClientUtil.execute(HttpClientUtil.getHttpClient(), "http://localhost:80/gradle/count", null, 1000);
        System.out.println(new String(execute));
    }
    
    @Test
    public void execute(){
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(50000)  //连接时间
                .setConnectionRequestTimeout(50000) //connect Manager(连接池)获取Connection 超时时间
                .setSocketTimeout(50000) //请求获取数据的超时时间(即响应时间)
                .build();
        HttpPost httpPost = new HttpPost("http://127.0.0.1:8080/simpleRPC");
        httpPost.setConfig(requestConfig);
        httpPost.setEntity(EntityBuilder.create().setBinary("weeqeqw".getBytes()).setContentEncoding("UTF-8").build());
//        httpPost.setHeader("Content-Type", "multipart/form-data");
    
//        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
//        builder.addTextBody("json", jsonParam.toString(), ContentType.MULTIPART_FORM_DATA);
    
        CloseableHttpResponse response = null;
        try {
            response = HttpClientUtil.getHttpClient().execute(httpPost);
        } catch (IOException e) {
            throw new RPCException(e);
        }
    
        HttpEntity entity = response.getEntity();
        System.out.println(entity);
    }
    
    public static void main(String[] args) {
        System.out.println(ClusterClient.class.getName().replace(ClusterClient.class.getSimpleName(), ""));
    }
}
