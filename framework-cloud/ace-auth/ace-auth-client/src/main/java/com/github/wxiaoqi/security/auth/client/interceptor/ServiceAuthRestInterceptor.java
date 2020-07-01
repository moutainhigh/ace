package com.github.wxiaoqi.security.auth.client.interceptor;

import com.alibaba.fastjson.JSON;
import com.github.ag.core.util.jwt.IJWTInfo;
import com.github.wxiaoqi.security.auth.client.annotation.CheckClientToken;
import com.github.wxiaoqi.security.auth.client.annotation.IgnoreClientToken;
import com.github.wxiaoqi.security.auth.client.config.ServiceAuthConfig;
import com.github.wxiaoqi.security.auth.client.jwt.ServiceAuthUtil;
import com.github.wxiaoqi.security.common.constant.RestCodeConstants;
import com.github.wxiaoqi.security.common.exception.auth.ClientTokenException;
import com.github.wxiaoqi.security.common.msg.BaseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 服务token认证
 *
 * @author ace
 * @version 2017/9/12
 */
@SuppressWarnings("ALL")
public class ServiceAuthRestInterceptor extends HandlerInterceptorAdapter {
    private Logger logger = LoggerFactory.getLogger(ServiceAuthRestInterceptor.class);

    @Autowired
    private ServiceAuthUtil serviceAuthUtil;

    @Autowired
    private ServiceAuthConfig serviceAuthConfig;

    private List<String> allowedClient;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String method = request.getMethod();
        String questURL = request.getRequestURL().toString();
        if (HttpMethod.OPTIONS.matches(method)) {
            return super.preHandle(request, response, handler);
        }
        if (!(handler instanceof HandlerMethod)) {
            return super.preHandle(request, response, handler);
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        // 配置该注解，说明不进行服务拦截
        CheckClientToken annotation = handlerMethod.getBeanType().getAnnotation(CheckClientToken.class);
        IgnoreClientToken ignoreClientToken = handlerMethod.getMethodAnnotation(IgnoreClientToken.class);
        if (annotation == null) {
            annotation = handlerMethod.getMethodAnnotation(CheckClientToken.class);
        }
        if (annotation == null || ignoreClientToken != null) {
            return super.preHandle(request, response, handler);
        } else {
            String token = request.getHeader(serviceAuthConfig.getTokenHeader());
            try {
                IJWTInfo infoFromToken = serviceAuthUtil.getInfoFromToken(token,questURL);
                String uniqueName = infoFromToken.getUniqueName();
                for (String client : serviceAuthUtil.getAllowedClient()) {
                    if (client.equals(uniqueName)) {
                        return super.preHandle(request, response, handler);
                    }
                }
            } catch (ClientTokenException ex) {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                logger.error(ex.getMessage(), ex);
                response.setContentType("UTF-8");
                response.getOutputStream().println(JSON.toJSONString(new BaseResponse(ex.getStatus(), ex.getMessage())));
                return false;
            }
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.getOutputStream().println(JSON.toJSONString(new BaseResponse(RestCodeConstants.EX_CLIENT_FORBIDDEN_CODE, "Client is Forbidden!")));
            return false;
        }
    }
}
