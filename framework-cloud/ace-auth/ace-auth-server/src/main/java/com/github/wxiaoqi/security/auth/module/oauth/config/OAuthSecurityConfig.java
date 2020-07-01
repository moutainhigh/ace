package com.github.wxiaoqi.security.auth.module.oauth.config;

import com.github.ag.core.constants.CommonConstants;
import com.github.ag.core.util.RsaKeyHelper;
import com.github.wxiaoqi.security.auth.configuration.KeyConfiguration;
import com.github.wxiaoqi.security.auth.feign.AppUserService;
import com.github.wxiaoqi.security.auth.feign.LogService;
import com.github.wxiaoqi.security.auth.jwt.user.JwtTokenUtil;
import com.github.wxiaoqi.security.auth.module.oauth.bean.OauthUser;
import com.github.wxiaoqi.security.auth.module.oauth.vo.HouseInfoVo;
import com.github.wxiaoqi.security.common.constant.RedisKeyConstants;
import com.github.wxiaoqi.security.common.util.ToolUtil;
import com.github.wxiaoqi.security.common.vo.log.LogInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import sun.security.rsa.RSAPrivateCrtKeyImpl;
import sun.security.rsa.RSAPublicKeyImpl;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class OAuthSecurityConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private AuthenticationManager auth;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private RsaKeyHelper rsaKeyHelper;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private KeyConfiguration keyConfiguration;
    @Autowired
    private LogService logService;

    @Autowired
    protected HttpServletRequest request;
//    @Autowired
//    private RedisConnectionFactory redisConnectionFactory;

//    @Bean
//    public RedisTokenStore redisTokenStore(){
//        RedisTokenStore redisTokenStore = new RedisTokenStore(redisConnectionFactory);
//        redisTokenStore.setPrefix("AG:OAUTH:");
//        return redisTokenStore;
//    }

    @Bean
    public JwtTokenStore jwtTokenStore() throws NoSuchAlgorithmException, InvalidKeyException, IOException {
        JwtTokenStore jwtTokenStore = new JwtTokenStore(accessTokenConverter());
        return jwtTokenStore;
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security)
            throws Exception {
        security
                .tokenKeyAccess("permitAll()");
        security.checkTokenAccess("isAuthenticated()");
        //需要更换成加密模式
        security.passwordEncoder(NoOpPasswordEncoder.getInstance());
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints)
            throws Exception {
        endpoints
                .authenticationManager(auth)
                .tokenStore(jwtTokenStore()).accessTokenConverter(accessTokenConverter())
        ;
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients)
            throws Exception {
        //需要更换成加密模式
        clients.jdbc(dataSource)
                .passwordEncoder(NoOpPasswordEncoder.getInstance());
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() throws IOException, InvalidKeyException, NoSuchAlgorithmException {
        byte[] pri, pub = null;
        try {
            pri = rsaKeyHelper.toBytes(redisTemplate.opsForValue().get(RedisKeyConstants.REDIS_USER_PRI_KEY).toString());
            pub = rsaKeyHelper.toBytes(redisTemplate.opsForValue().get(RedisKeyConstants.REDIS_USER_PUB_KEY).toString());
        } catch (Exception e) {
            Map<String, byte[]> keyMap = rsaKeyHelper.generateKey(keyConfiguration.getUserSecret());
            redisTemplate.opsForValue().set(RedisKeyConstants.REDIS_USER_PRI_KEY, rsaKeyHelper.toHexString(keyMap.get("pri")));
            redisTemplate.opsForValue().set(RedisKeyConstants.REDIS_USER_PUB_KEY, rsaKeyHelper.toHexString(keyMap.get("pub")));
            pri = keyMap.get("pri");
            pub = keyMap.get("pub");
        }
        JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter() {
            /***
             * 重写增强token方法,用于自定义一些token返回的信息
             */
            @Override
            public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
                OauthUser user = (OauthUser) authentication.getUserAuthentication().getPrincipal();// 与登录时候放进去的UserDetail实现类一直查看link{SecurityConfiguration}
                /** 自定义一些token属性 ***/
                final Map<String, Object> additionalInformation = new HashMap<>();
                Date expireTime = DateTime.now().plusSeconds(jwtTokenUtil.getExpire()).toDate();
                additionalInformation.put(CommonConstants.JWT_KEY_EXPIRE, expireTime);
                additionalInformation.put(CommonConstants.JWT_KEY_USER_ID, user.getId());
                additionalInformation.put(CommonConstants.JWT_KEY_TENANT_ID, user.getTenantId());
                additionalInformation.put(CommonConstants.JWT_KEY_DEPART_ID, user.getDepartId());
                additionalInformation.put(CommonConstants.JWT_KEY_NAME, user.getName());
                additionalInformation.put("sub", user.getUsername());
                ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInformation);
                OAuth2AccessToken enhancedToken = super.enhance(accessToken, authentication);

                LogInfoVo logInfoVo = new LogInfoVo();
                logInfoVo.setLogType("1");
                logInfoVo.setLogName("登录");
                logInfoVo.setOs("1");
                logInfoVo.setCreateTime(new Date());
                logInfoVo.setCreateBy(user.getId());
                logInfoVo.setUserType(user.getUserType());
                String ip = "";
                try {
                    ip = ToolUtil.getIpAddr(request);
                    logInfoVo.setIp(ip);
                    logService.savelog(logInfoVo);
                }catch (Exception e){
                }

                return enhancedToken;
            }

        };
        accessTokenConverter.setKeyPair(new KeyPair(new RSAPublicKeyImpl(pub), RSAPrivateCrtKeyImpl.newKey(pri)));
        return accessTokenConverter;
    }
}