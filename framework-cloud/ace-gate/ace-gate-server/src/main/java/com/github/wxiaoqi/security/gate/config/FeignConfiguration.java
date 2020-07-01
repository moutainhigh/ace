package com.github.wxiaoqi.security.gate.config;

import com.github.wxiaoqi.security.auth.client.interceptor.ServiceFeignInterceptor;
import org.springframework.context.annotation.Bean;

/**
 * @author ace
 * @date 2017/9/12
 */
public class FeignConfiguration {
    @Bean
    ServiceFeignInterceptor getClientTokenInterceptor() {
        return new ServiceFeignInterceptor();
    }
}
