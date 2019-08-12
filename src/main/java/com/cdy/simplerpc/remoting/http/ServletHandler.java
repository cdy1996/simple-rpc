package com.cdy.simplerpc.remoting.http;

import com.cdy.simplerpc.proxy.Invocation;
import com.cdy.simplerpc.proxy.Invoker;
import com.cdy.simplerpc.remoting.RPCContext;
import com.cdy.simplerpc.remoting.RPCRequest;
import com.cdy.simplerpc.remoting.RPCResponse;
import com.cdy.simplerpc.serialize.ISerialize;
import com.cdy.simplerpc.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
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
    
    private final ISerialize serialize;
    
    public ServletHandler(Map<String, Invoker> handlerMap, ISerialize serialize) {
        this.handlerMap = handlerMap;
        this.serialize = serialize;
    }
    
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            
            RPCRequest rpcRequest = null;
//            if (req.getContentType().contains("json")) {
//                String params = req.getParameter("params");
//                rpcRequest = (RPCRequest) serialize.deserialize(params, RPCRequest.class);
//            } else if (req.getContentType().contains("octet-stream")) {
            Part params = req.getPart("params");
            rpcRequest = (RPCRequest) serialize.deserialize(StringUtil.inputStreamToBytes(params.getInputStream()), RPCRequest.class);
//            }
            
            log.info("接受到请求" + rpcRequest);
            RPCContext context = RPCContext.current();
            Map<String, Object> contextMap = context.getMap();
            
            String className = rpcRequest.getClassName();
            Invoker o = handlerMap.get(className);
            Invocation invocation = new Invocation(rpcRequest.getMethodName(), rpcRequest.getParams(), rpcRequest.getTypes());
            contextMap.putAll(rpcRequest.getAttach());
            Object result = o.invoke(invocation);
            
            RPCResponse rpcResponse = new RPCResponse();
            rpcResponse.setAttach(contextMap);
            rpcResponse.setRequestId(rpcRequest.getRequestId());
            rpcResponse.setResultData(result);

//            if (result instanceof byte[] || result instanceof File || result instanceof OutputStream) {
            try (ServletOutputStream outputStream = resp.getOutputStream()) {
                resp.setContentType("application/octet-stream");
                resp.setCharacterEncoding("UTF-8");
                outputStream.write((byte[]) serialize.serialize(rpcResponse, RPCResponse.class));
                outputStream.flush();
            }
//            } else {
//                try (PrintWriter writer = resp.getWriter()) {
//                    resp.setContentType("application/json");
//                    resp.setCharacterEncoding("UTF-8");
//                    writer.write((String) serialize.serialize(rpcResponse, RPCResponse.class));
//                    writer.flush();
//                }
//            }
            
        } catch (Exception e) {
            try (PrintWriter writer = resp.getWriter()) {
                resp.setContentType("application/octet-stream");
                resp.setCharacterEncoding("UTF-8");
                writer.write(e.toString());
                writer.flush();
            }
        }
    }
}