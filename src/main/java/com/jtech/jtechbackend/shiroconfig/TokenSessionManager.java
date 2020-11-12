package com.jtech.jtechbackend.shiroconfig;

import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.util.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.Serializable;


/**
 * token
 * @author     xinchuang
 * @date       2020/6/5 17:57
 * @version    v1.0
 * @since      @Copyright(c) 爱睿智健康科技(北京)有限公司
 */
public class TokenSessionManager extends DefaultWebSessionManager {

    private static final String TOKEN_HEADER_NAME = "token";

    @Override
    protected Serializable getSessionId(ServletRequest request, ServletResponse response) {
        // 从前端头里获取token
        String token = WebUtils.toHttp(request).getHeader("token");
        if(!StringUtils.hasText(token)){
//            token = UUID.randomUUID().toString();
            token = "william";
        }
        return token;
    }
}
