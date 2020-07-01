package com.github.wxiaoqi.security.common.exception.auth;


import com.github.ag.core.exception.BaseException;
import com.github.wxiaoqi.security.common.constant.RestCodeConstants;

/**
 * Created by ace on 2017/9/10.
 */
public class ClientInvalidException extends BaseException {
    public ClientInvalidException(String message) {
        super(message, RestCodeConstants.EX_CLIENT_INVALID_CODE);
    }
}
