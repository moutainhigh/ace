package com.github.wxiaoqi.security.gate;

import com.github.wxiaoqi.security.auth.client.EnableAceAuthClient;
import com.spring4all.swagger.EnableSwagger2Doc;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author ace
 * @version 2017/12/26.
 */
@EnableEurekaClient
@SpringBootApplication
// 开启事务
@EnableTransactionManagement
// 开启熔断监控
@EnableCircuitBreaker
// 开启服务鉴权
@ComponentScan({"com.github.wxiaoqi.security.gate"})
@EnableFeignClients({"com.github.wxiaoqi.security.auth.client.feign", "com.github.wxiaoqi.security.gate.feign"})
@MapperScan("com.github.wxiaoqi.security.gate.mapper")
@EnableAceAuthClient
@EnableSwagger2Doc
public class Demo1Bootstrap {
    public static void main(String[] args) {
        new SpringApplicationBuilder(Demo1Bootstrap.class).web(true).run(args);
    }


}
