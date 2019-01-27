package com.cdy.simplerpc.remoting.servlet;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;

import javax.net.ssl.SSLContext;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

/**
 * todo
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
    public static void setPostParams(HttpPost httpPost, String json) {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("params", json));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    
    public static CloseableHttpClient getHttpClient() {
        return HttpClients.custom().setConnectionManager(manager).build();
    }
    
    /**
     * 获取忽略证书的HttpClient
     *
     * @return
     */
    public static CloseableHttpClient getHttpsClient() {
        return getHttpsClient(createSSLConnSocketFactory());
    }
    
    public static CloseableHttpClient getHttpsClient(SSLConnectionSocketFactory sslSocketFactory) {
        return HttpClients.custom().setSSLSocketFactory(sslSocketFactory).setConnectionManager(manager).build();
    }
    
    
    /**
     * 创建SSL安全连接
     *
     * @return
     */
    private static SSLConnectionSocketFactory createSSLConnSocketFactory() {
        SSLConnectionSocketFactory sslsf = null;
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    // 信任所有
                    return true;
                }
                
            }).build();
            sslsf = new SSLConnectionSocketFactory(sslContext);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return sslsf;
    }
    
    public static void close() {
        manager.close();
    }
}
