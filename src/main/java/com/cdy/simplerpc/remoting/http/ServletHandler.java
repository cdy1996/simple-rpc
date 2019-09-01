package com.cdy.simplerpc.remoting.http;

import com.alibaba.nacos.client.utils.JSONUtils;
import com.cdy.simplerpc.proxy.Invoker;
import com.cdy.simplerpc.remoting.RPCRequest;
import com.cdy.simplerpc.remoting.RPCResponse;
import com.cdy.simplerpc.netty.rpc.RPCContext;
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
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static com.cdy.simplerpc.util.StringUtil.UTF8;

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
                bytes = req.getParameter("params").getBytes(StandardCharsets.UTF_8);
            } else {
                bytes = StringUtil.inputStreamToBytes(req.getInputStream());
            }
            RPCRequest rpcRequest = serialize.deserialize(bytes, RPCRequest.class);
        
            log.info("接受到请求 {}" ,rpcRequest);
            RPCContext context = new RPCContext();
            String contextString = req.getHeader("context");
            Map<String, Object> contextMap = (Map<String, Object>) JSONUtils.deserializeObject(contextString, Map.class);
            context.setAttach(contextMap);
            RPCContext.set(context);

            rpcResponse = new RPCResponse();

            String className = rpcRequest.getClassName();
            Invoker o = handlerMap.get(className);
            Object result = o.invoke(rpcRequest.toInvocation());
        
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
                resp.setCharacterEncoding(UTF8);
                if (e instanceof RPCResponse) {
                    writer.write(new String(serialize.serialize((RPCResponse) e, RPCResponse.class), StandardCharsets.UTF_8));
                }
                writer.flush();
            }
        } else {
            try (ServletOutputStream outputStream = resp.getOutputStream()) {
                resp.setContentType("application/octet-stream");
                resp.setCharacterEncoding(UTF8);
                if (e instanceof RPCResponse) {
                    outputStream.write(serialize.serialize((RPCResponse) e, RPCResponse.class));
                }
                outputStream.flush();
            }
        }
        
        
    }
    
    
}