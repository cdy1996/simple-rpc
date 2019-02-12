package com.cdy.simplerpc.remoting.jetty;

import com.cdy.serialization.JsonUtil;
import com.cdy.simplerpc.proxy.Invocation;
import com.cdy.simplerpc.remoting.AbstractClient;
import com.cdy.simplerpc.remoting.RPCContext;
import com.cdy.simplerpc.remoting.RPCRequest;
import com.cdy.simplerpc.remoting.RPCResponse;
import com.cdy.simplerpc.util.StringUtil;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;

import static com.cdy.simplerpc.remoting.jetty.HttpClientUtil.getHttpClient;
import static com.cdy.simplerpc.remoting.jetty.HttpClientUtil.setPostParams;
import static com.cdy.simplerpc.util.StringUtil.getServer;
import static com.cdy.simplerpc.util.StringUtil.inputStreamToString;

/**
 * 客户端
 * Created by 陈东一
 * 2018/11/25 0025 14:28
 */
public class HttpClient extends AbstractClient {
    
    
    @Override
    public void init() {}
    
    public HttpClient() {
        init();
    }
    
    @Override
    public Object invoke(Invocation invocation) throws Exception {
        RPCRequest rpcRequest = invocation.toRequest();
        
        String serviceName = invocation.getInterfaceClass().getName();
        String address = getServiceDiscovery().discovery(serviceName);
    
        StringUtil.TwoResult<String, Integer> server = getServer(address);

        CloseableHttpClient client = getHttpClient(Math.toIntExact(getClientBootStrap().getRemotingConfig().getInvokeTimeout()));
        // 隐式传递参数
        RPCContext rpcContext1 = RPCContext.current();
        rpcRequest.setAttach(rpcContext1.getMap());
        rpcRequest.getAttach().put("address", address);
        
        HttpPost httpPost = new HttpPost("http://" + server.getFirst() + ":" + server.getSecond() + "/simpleRPC");
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
