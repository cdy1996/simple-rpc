package com.cdy.simplerpc.remoting.http;

import com.cdy.simplerpc.proxy.Invocation;
import com.cdy.simplerpc.proxy.Invoker;
import com.cdy.simplerpc.remoting.RPCContext;
import com.cdy.simplerpc.remoting.RPCRequest;
import com.cdy.simplerpc.remoting.RPCResponse;
import com.cdy.simplerpc.serialize.ISerialize;
import com.cdy.simplerpc.serialize.JsonSerialize;
import com.cdy.simplerpc.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
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
public class ServletHandler extends HttpServlet{
    
    private final Map<String, Invoker> handlerMap;
    
    private final ISerialize serialize;
    
    public ServletHandler(Map<String, Invoker> handlerMap, ISerialize serialize) {
        this.handlerMap = handlerMap;
        this.serialize = serialize;
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RPCResponse rpcResponse = new RPCResponse();
        try {
            byte[] bytes;
            if (serialize instanceof JsonSerialize) {
                bytes = req.getParameter("params").getBytes("utf-8");
            } else {
                bytes = StringUtil.inputStreamToBytes(req.getInputStream());
            }
            RPCRequest rpcRequest = serialize.deserialize(bytes, RPCRequest.class);
        
            log.info("接受到请求 {}" ,rpcRequest);
            String requestId = rpcRequest.getRequestId();
            RPCContext context = RPCContext.current();
            Map<String, Object> contextMap = context.getMap();
        
            rpcResponse = new RPCResponse();
            rpcResponse.setAttach(contextMap);
            rpcResponse.setRequestId(requestId);
        
            String className = rpcRequest.getClassName();
            Invoker o = handlerMap.get(className);
            Invocation invocation = new Invocation(rpcRequest.getMethodName(), rpcRequest.getParams(), rpcRequest.getTypes());
            contextMap.putAll(rpcRequest.getAttach());
            Object result = o.invoke(invocation);
        
            rpcResponse.setResultData(result);
        
            write(resp, rpcResponse);
        } catch (Exception e) {
            rpcResponse.setResultData(e);
            write(resp, rpcResponse);
        }
    }
    

    
    private void write(HttpServletResponse resp, Object e) throws IOException {
        
        if (serialize instanceof JsonSerialize) {
            try (PrintWriter writer = resp.getWriter()) {
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                if (e instanceof RPCResponse) {
                    writer.write(new String(serialize.serialize((RPCResponse) e, RPCResponse.class), "utf-8"));
                }
                writer.flush();
            }
        } else {
            try (ServletOutputStream outputStream = resp.getOutputStream()) {
                resp.setContentType("application/octet-stream");
                resp.setCharacterEncoding("UTF-8");
                if (e instanceof RPCResponse) {
                    outputStream.write(serialize.serialize((RPCResponse) e, RPCResponse.class));
                }
                outputStream.flush();
            }
        }
        
        
    }
    
    
}