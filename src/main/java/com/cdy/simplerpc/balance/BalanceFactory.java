package com.cdy.simplerpc.balance;

import com.cdy.simplerpc.config.PropertySources;
import com.netflix.loadbalancer.RandomRule;
import com.netflix.loadbalancer.RoundRobinRule;
import lombok.extern.slf4j.Slf4j;

/**
 * 服务创建工厂
 * <p>
 * Created by 陈东一
 * 2019/5/25 0025 19:51
 */
@Slf4j
public class BalanceFactory {
    
    public static IBalance createBalance(PropertySources propertySources) {
        
        String balance = propertySources.resolveProperty("balance");
        String rule = propertySources.resolveProperty("rule");
        log.info("创建负载均衡器 {} {}", balance, rule);
        // todo spi
        if ("ribbon".equalsIgnoreCase(balance)) {
            if ("round".equalsIgnoreCase(rule)) {
                return new RibbonBalance(new RoundRobinRule());
            } else if ("random".equalsIgnoreCase(rule)) {
                return new RibbonBalance(new RandomRule());
            } else {
                return new RibbonBalance();
            }
        } else {
            return new SimpleBalance();
        }
        
    }
}
