package com.github.wxiaoqi.security.external.filter;

import com.github.wxiaoqi.security.auth.client.interceptor.RequestWrapper;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 过滤器Filter，用来把request传递下去
 */
@WebFilter(urlPatterns = "/*",filterName = "channelFilter")
public class ChannelFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }
 
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        ServletRequest requestWrapper = null;
        if(servletRequest instanceof HttpServletRequest) {
            requestWrapper = new RequestWrapper((HttpServletRequest) servletRequest);
        }
        if(requestWrapper == null) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            filterChain.doFilter(requestWrapper, servletResponse);
        }
    }
 
    @Override
    public void destroy() {

    }
}