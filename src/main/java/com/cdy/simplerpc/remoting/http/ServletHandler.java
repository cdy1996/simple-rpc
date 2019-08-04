package com.cdy.simplerpc.remoting.http;

import com.cdy.serialization.JsonUtil;
import com.cdy.simplerpc.exception.RPCException;
import com.cdy.simplerpc.proxy.Invocation;
import com.cdy.simplerpc.proxy.Invoker;
import com.cdy.simplerpc.remoting.RPCContext;
import com.cdy.simplerpc.remoting.RPCRequest;
import com.cdy.simplerpc.remoting.RPCResponse;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * 服务端处理
 * Created by 陈东一
 * 2019/1/27 0027 14:47
 */
@Slf4j
public class ServletHandler extends HttpServlet {
    
    private final Map<String, Invoker> handlerMap;
    
    public ServletHandler(Map<String, Invoker> handlerMap) {
        this.handlerMap=handlerMap;
    }
    
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        
        String params = req.getParameter("params");
        RPCRequest msg1 = JsonUtil.parseObject(params, RPCRequest.class);
    
        log.info("接受到请求" + msg1);
    
        RPCContext context = RPCContext.current();
        Map<String, Object> contextMap = context.getMap();
        
        String className = msg1.getClassName();
        Object result = null;

        if (handlerMap.containsKey(className)) {
            Invoker o = handlerMap.get(className);
            Invocation invocation = new Invocation(msg1.getMethodName(), msg1.getParams(), msg1.getTypes());
            contextMap.putAll(msg1.getAttach());
            try {
                result = o.invoke(invocation);
            } catch (Exception e) {
                // 理论上不会抛异常 因为可能的异常都在异常过滤器中处理掉了
                throw new RPCException(e);
            }
        }
    
        RPCResponse rpcResponse = new RPCResponse();
        rpcResponse.setAttach(contextMap);
        rpcResponse.setRequestId(msg1.getRequestId());
        rpcResponse.setResultData(result);
        
        try (PrintWriter writer = resp.getWriter()) {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            String json = JsonUtil.toString(rpcResponse);
            writer.write(json);
            writer.flush();
        }
    }
}
