package com.cdy.simplerpc.remoting.servlet;

import com.cdy.serialization.JsonUtil;
import com.cdy.simplerpc.proxy.Invocation;
import com.cdy.simplerpc.registry.IServiceDiscovery;
import com.cdy.simplerpc.remoting.Client;
import com.cdy.simplerpc.remoting.RPCContext;
import com.cdy.simplerpc.remoting.RPCRequest;
import com.cdy.simplerpc.remoting.RPCResponse;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;

import static com.cdy.simplerpc.remoting.servlet.HttpClientUtil.getHttpClient;
import static com.cdy.simplerpc.remoting.servlet.HttpClientUtil.setPostParams;
import static com.cdy.simplerpc.remoting.servlet.StringUtil.inputStreamToString;

/**
 * 客户端
 * Created by 陈东一
 * 2018/11/25 0025 14:28
 */
public class HttpClient implements Client {
    
    private IServiceDiscovery serviceDiscovery;
    
    
    @Override
    public void init() {
    
    }
    
    public HttpClient(IServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
        init();
    }
    
    @Override
    public Object invoke(Invocation invocation) throws Exception {
        RPCRequest rpcRequest = invocation.toRequest();
        
        String serviceName = invocation.getInterfaceClass().getName();
        String address = serviceDiscovery.discovery(serviceName);
        
        String[] addres = address.split(":");
        String ip = addres[0];
        int port = Integer.parseInt(addres[1]);
        
        CloseableHttpClient client = getHttpClient();
        // 隐式传递参数
        RPCContext rpcContext1 = RPCContext.current();
        rpcRequest.setAttach(rpcContext1.getMap());
        rpcRequest.getAttach().put("address", address);
        
        HttpPost httpPost = new HttpPost("http://" + ip + ":" + port + "/simpleRPC");
        setPostParams(httpPost, JsonUtil.toString(rpcRequest));
        CloseableHttpResponse response = client.execute(httpPost);
        
        HttpEntity entity = response.getEntity();
        String result = inputStreamToString(entity.getContent());
        
        RPCResponse rpcResponse = JsonUtil.parseObject(result, RPCResponse.class);
        // 隐式接受参数
        RPCContext rpcContext = RPCContext.current();
        rpcContext.setMap(rpcResponse.getAttach());
        return result;
    }
    
    
    @Override
    public void close() {
        HttpClientUtil.close();
    }
    
}
