package com.cdy.simplerpc.remoting.jetty;

import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;

import javax.net.ssl.SSLContext;
import java.util.ArrayList;
import java.util.List;

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
    
    public static CloseableHttpClient getHttpClient(Integer timeout) {
        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setSocketTimeout(timeout)
                .setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout)
                .setStaleConnectionCheckEnabled(true)
                .build();
        return HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig).setConnectionManager(manager).build();
    }
    
    /**
     * 获取忽略证书的HttpClient
     *
     * @return
     */
    public static CloseableHttpClient getHttpsClient(Integer timeout) throws Exception {
        return getHttpsClient(timeout, createSSLConnSocketFactory());
    }
    
    public static CloseableHttpClient getHttpsClient(Integer timeout, SSLConnectionSocketFactory sslSocketFactory) {
        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setSocketTimeout(timeout)
                .setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout)
                .setStaleConnectionCheckEnabled(true)
                .build();
        return HttpClients.custom().setSSLSocketFactory(sslSocketFactory).setConnectionManager(manager).build();
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
}
