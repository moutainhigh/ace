package com.github.wxiaoqi.security.app.oauth.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.security.Principal;

/**
 * @author ace
 * @create 2018/3/19.
 */
@RestController
@ApiIgnore
public class UserController {

    @RequestMapping("/user")
    public Principal userInfo(Principal principal) {
        return principal;
    }
}
