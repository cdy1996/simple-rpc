package com.cdy.simplerpc.remoting.http.httpClient;

import com.cdy.simplerpc.exception.RPCException;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.cdy.simplerpc.util.StringUtil.inputStreamToString;

/**
 * httpclient 工具类
 *
 * Created by 陈东一
 * 2019/1/27 0027 14:43
 */
public class HttpClientUtil {
    private static PoolingHttpClientConnectionManager manager;
    
    
    static {
        manager = new PoolingHttpClientConnectionManager();
        manager.setMaxTotal(400);
        manager.setDefaultMaxPerRoute(50);
    }
    
    /**
     * 设置post请求的参数
     */
    public static void setPostParams(HttpPost httpPost, String json) throws Exception {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("params", json));
        httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
        
    }
    
    public static CloseableHttpClient getHttpClient() {
        return HttpClients.custom()
                .setConnectionManager(manager)
                .setRetryHandler(new DefaultHttpRequestRetryHandler(0,false))
                .build();
    }
    
    /**
     * 获取忽略证书的HttpClient
     *
     * @return
     */
    public static CloseableHttpClient getHttpsClient() throws Exception {
        return getHttpsClient(createSSLConnSocketFactory());
    }
    
    public static CloseableHttpClient getHttpsClient(SSLConnectionSocketFactory sslSocketFactory) {
        return HttpClients.custom().setSSLSocketFactory(sslSocketFactory)
                .setConnectionManager(manager)
                .setRetryHandler(new DefaultHttpRequestRetryHandler(0,false))
                .build();
    }
    
    
    /**
     * 创建SSL安全连接
     *
     * @return
     */
    private static SSLConnectionSocketFactory createSSLConnSocketFactory() throws Exception {
        SSLConnectionSocketFactory sslsf = null;
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, (chain, authType) -> {
                // 信任所有
                return true;
            }).build();
            sslsf = new SSLConnectionSocketFactory(sslContext);
        return sslsf;
    }
    
    public static void close() {
        manager.close();
    }
    
    public static String execute(CloseableHttpClient client, String uri, String params, Integer timeout) throws Exception {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(timeout)  //连接时间
                .setConnectionRequestTimeout(timeout) //connect Manager(连接池)获取Connection 超时时间
                .setSocketTimeout(timeout) //请求获取数据的超时时间(即响应时间)
                .build();
        HttpPost httpPost = new HttpPost(uri);
        httpPost.setConfig(requestConfig);
        setPostParams(httpPost, params);
        CloseableHttpResponse response = null;
        try {
            response = client.execute(httpPost);
        } catch (IOException e) {
            throw new RPCException(e);
        }
    
        HttpEntity entity = response.getEntity();
        if (response.getStatusLine().getStatusCode() == 200) {
            return inputStreamToString(entity.getContent());
        } else {
            throw new RPCException("调用失败 ==> " + response.getStatusLine().getStatusCode());
        }
        
       
    }
}
