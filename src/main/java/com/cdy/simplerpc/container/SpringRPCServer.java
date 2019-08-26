package com.cdy.simplerpc.container;

import com.cdy.simplerpc.ServerBootStrap;
import com.cdy.simplerpc.config.SpringPropertySource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.env.Environment;

import java.util.Map;

@Slf4j
public class SpringRPCServer implements SmartLifecycle, ApplicationContextAware {

    private ServerBootStrap rpc;
    private ApplicationContext applicationContext;
    private volatile boolean isRunning = false;

    @Override
    public void start() {
        Map<String, RPCServerFactoryBean> beansOfType = applicationContext.getBeansOfType(RPCServerFactoryBean.class);

        Object[] objects = beansOfType.entrySet().stream().map(Map.Entry::getValue).toArray();

        try {
            Environment environment = applicationContext.getEnvironment();
           rpc = ServerBootStrap
                    .build(new SpringPropertySource(environment))
                    //                .registry("nacos-1", "127.0.0.1:8848", "529469ac-0341-4276-a256-14dcf863935c")
                    //                .registry("zookeeper-1", "127.0.0.1:2181", "/registry")
                    .target(objects)
//                    .port(environment.getProperty("rpc.port")) //暴露默认端口
//                    .ip(environment.getProperty("rpc.ip")) //暴露默认ip
                    //                .protocols("http")
//                    .protocols(environment.getProperty("rpc.protocols")) //暴露默认协议
                    .start();
            isRunning = true;
        } catch (Exception e) {
           log.error(e.getMessage() ,e );
        }
    }

    @Override
    public void stop() {
        rpc.closeAll();
        isRunning= false;
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(Runnable callback) {
        stop();
        callback.run();
    }

    @Override
    public int getPhase() {
        return 0;
    }
}
