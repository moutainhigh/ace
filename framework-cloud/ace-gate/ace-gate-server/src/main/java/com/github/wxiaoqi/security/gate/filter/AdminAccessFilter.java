package com.github.wxiaoqi.security.gate.filter;

import com.alibaba.fastjson.JSON;
import com.github.ag.core.context.BaseContextHandler;
import com.github.ag.core.util.jwt.IJWTInfo;
import com.github.wxiaoqi.security.api.vo.authority.PermissionInfo;
import com.github.wxiaoqi.security.auth.client.config.ServiceAuthConfig;
import com.github.wxiaoqi.security.auth.client.config.UserAuthConfig;
import com.github.wxiaoqi.security.auth.client.interceptor.RequestWrapper;
import com.github.wxiaoqi.security.auth.client.jwt.ServiceAuthUtil;
import com.github.wxiaoqi.security.auth.client.jwt.UserAuthUtil;
import com.github.wxiaoqi.security.common.constant.RequestHeaderConstants;
import com.github.wxiaoqi.security.common.exception.auth.NonLoginException;
import com.github.wxiaoqi.security.common.exception.auth.UserForbiddenException;
import com.github.wxiaoqi.security.common.util.ClientUtil;
import com.github.wxiaoqi.security.common.vo.log.LogInfoVo;
import com.github.wxiaoqi.security.gate.feign.IUserFeign;
import com.github.wxiaoqi.security.gate.feign.LogService;
import com.github.wxiaoqi.security.gate.utils.DBLog;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * ${DESCRIPTION}
 *
 * @author wanghaobin
 * @version 2017-06-23 8:25
 */
@Component
@Slf4j
public class AdminAccessFilter extends ZuulFilter {
    @Autowired
    @Lazy
    private IUserFeign userService;
    @Autowired
    @Lazy
    private LogService logService;

    @Value("${gate.ignore.startWith}")
    private String startWith;

    @Value("${zuul.prefix}")
    private String zuulPrefix;

    @Autowired
    private UserAuthUtil userAuthUtil;

    @Autowired
    private ServiceAuthConfig serviceAuthConfig;

    @Autowired
    private UserAuthConfig userAuthConfig;

    @Autowired
    private ServiceAuthUtil serviceAuthUtil;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        final String requestUri = request.getRequestURI().substring(zuulPrefix.length());
        final String method = request.getMethod();
        BaseContextHandler.setToken(null);
        // 不进行拦截的地址
        if (isStartWith(requestUri) || HttpMethod.OPTIONS.matches(method)) {
            return null;
        }

        String queryString = request.getQueryString();
        String params = "";
        //获取请求参数集合并进行遍历拼接

        if("POST".equals(method) || "PUT".equals(method)){
			RequestWrapper requestWrapper = new RequestWrapper(request);
			params = requestWrapper.getBody();
        }else if("GET".equals(method) || "DELETE".equals(method)){
            params = queryString;
        }

        List<PermissionInfo> permissionIfs = userService.getAllPermissionInfo();
        // 判断资源是否启用权限约束
        Stream<PermissionInfo> stream = getPermissionIfs(requestUri, method, permissionIfs);
        List<PermissionInfo> result = stream.collect(Collectors.toList());
        PermissionInfo[] permissions = result.toArray(new PermissionInfo[]{});
        if (permissions.length > 0) {
            IJWTInfo user = null;
            try {
                user = getJWTUser(request, ctx);
            } catch (Exception e) {
                setFailedRequest(JSON.toJSONString(new NonLoginException("用户身份过期,请重新登录!")), HttpStatus.UNAUTHORIZED.value());
                log.error(e.getMessage(), e);
                return null;
            }
            checkUserPermission(permissions, ctx, user, params);
            BaseContextHandler.remove();
        }
        // 申请客户端密钥头
        ctx.addZuulRequestHeader(serviceAuthConfig.getTokenHeader(), serviceAuthUtil.getClientToken());
        return null;
    }

    /**
     * 获取目标权限资源
     *
     * @param requestUri
     * @param method
     * @param serviceInfo
     * @return
     */
    private Stream<PermissionInfo> getPermissionIfs(final String requestUri, final String method, List<PermissionInfo> serviceInfo) {
        return serviceInfo.parallelStream().filter(new Predicate<PermissionInfo>() {
            @Override
            public boolean test(PermissionInfo permissionInfo) {
                String uri = permissionInfo.getUri();
                if (uri.indexOf("{") > 0) {
                    uri = uri.replaceAll("\\{\\*\\}", "[a-zA-Z\\\\d]+");
                }
                String regEx = "^" + uri + "$";
                return (Pattern.compile(regEx).matcher(requestUri).find())
                        && method.equals(permissionInfo.getMethod());
            }
        });
    }

    private void setCurrentUserInfoAndLog(RequestContext ctx, IJWTInfo user, PermissionInfo pm ,String params) {
        String host = ClientUtil.getClientIp(ctx.getRequest());
        LogInfoVo logInfo = new LogInfoVo();
        //pm.getMenu(), pm.getName(), pm.getUri(), new Date(), user.getId(), user.getName(), host, user.getOtherInfo().get(CommonConstants.JWT_KEY_TENANT_ID)
		logInfo.setLogType("3");
		logInfo.setIp(host);
		logInfo.setCreateBy(user.getId());
		logInfo.setCreateTime(new Date());
		logInfo.setBusName(pm.getMenu());
		logInfo.setOpt(pm.getName());
		logInfo.setUri(pm.getUri());
		logInfo.setParams(params);
        DBLog.getInstance().setLogService(logService).offerQueue(logInfo);
    }

    /**
     * 返回session中的用户信息
     *
     * @param request
     * @param ctx
     * @return
     */
    private IJWTInfo getJWTUser(HttpServletRequest request, RequestContext ctx) throws Exception {
        String authToken = request.getHeader(userAuthConfig.getTokenHeader());
        String originToken = null;
        if (StringUtils.isBlank(authToken)) {
            authToken = request.getParameter("token");
        }
        if (authToken != null && authToken.startsWith(RequestHeaderConstants.JWT_TOKEN_TYPE)) {
            originToken = authToken;
            authToken = authToken.substring(RequestHeaderConstants.JWT_TOKEN_TYPE.length(), authToken.length());
        }
        ctx.addZuulRequestHeader(userAuthConfig.getTokenHeader(), originToken);
        BaseContextHandler.setToken(originToken);
        return userAuthUtil.getInfoFromToken(authToken);
    }


    private void checkUserPermission(PermissionInfo[] permissions, RequestContext ctx, IJWTInfo user,String params) {
        List<PermissionInfo> permissionInfos = userService.getPermissionByUsername();
        PermissionInfo current = null;
        for (PermissionInfo info : permissions) {
            boolean anyMatch = permissionInfos.parallelStream().anyMatch(new Predicate<PermissionInfo>() {
                @Override
                public boolean test(PermissionInfo permissionInfo) {
                    return permissionInfo.getCode().equals(info.getCode());
                }
            });
            if (anyMatch) {
                current = info;
                break;
            }
        }
        if (current == null) {
            setFailedRequest(JSON.toJSONString(new UserForbiddenException("User Permission Forbidden!")), HttpStatus.FORBIDDEN.value());
        } else {
            if (!RequestMethod.GET.toString().equals(current.getMethod())) {
                setCurrentUserInfoAndLog(ctx, user, current,params);
            }
        }
    }


    /**
     * URI是否以什么打头
     *
     * @param requestUri
     * @return
     */
    private boolean isStartWith(String requestUri) {
        boolean flag = false;
        for (String s : startWith.split(",")) {
            if (requestUri.startsWith(s)) {
                return true;
            }
        }
        return flag;
    }

    /**
     * 网关抛异常
     *
     * @param body
     * @param code
     */
    private void setFailedRequest(String body, int code) {
        log.debug("Reporting error ({}): {}", code, body);
        RequestContext ctx = RequestContext.getCurrentContext();
        ctx.setResponseStatusCode(code);
        if (ctx.getResponseBody() == null) {
            ctx.setResponseBody(body);
            ctx.setSendZuulResponse(false);
            ctx.getResponse().setContentType("text/html;charset=UTF-8");
        }
    }

}
