package com.cdy.simplerpc.remoting.servlet;

import com.cdy.serialization.JsonUtil;
import com.cdy.simplerpc.proxy.Invocation;
import com.cdy.simplerpc.proxy.Invoker;
import com.cdy.simplerpc.remoting.RPCContext;
import com.cdy.simplerpc.remoting.RPCRequest;
import com.cdy.simplerpc.remoting.RPCResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static com.cdy.simplerpc.remoting.AbstractServer.handlerMap;

/**
 * todo
 * Created by 陈东一
 * 2019/1/27 0027 14:47
 */
public class ServletHandler extends HttpServlet {
    
    
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        String params = req.getParameter("params");
        RPCRequest msg1 = JsonUtil.parseObject(params, RPCRequest.class);
        
        System.out.println("接受到请求" + msg1);
        
        String className = msg1.getClassName();
        Object result = null;
        RPCResponse rpcResponse = new RPCResponse();
        if (handlerMap.containsKey(className)) {
            Invoker o = handlerMap.get(className);
            Invocation invocation = new Invocation(msg1.getMethodName(), msg1.getParams(), msg1.getTypes());
            invocation.setAttach(msg1.getAttach());
            try {
                result = o.invoke(invocation);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        RPCContext rpcContext = RPCContext.current();
        rpcResponse.setAttach(rpcContext.getMap());
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
