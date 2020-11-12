package com.jtech.jtechbackend.shiroconfig;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.DelegatingFilterProxy;
import redis.clients.jedis.JedisPoolConfig;

import java.util.LinkedHashMap;
import java.util.Map;


@Configuration
public class shiroConfig {

    @Value("${spring.redis.host:spring.redis.port}")
    private String redisHost;

    // 安全管理器  securityManager
    @Bean
    public DefaultWebSecurityManager defaultWebSecurityManager(UserRealm realm, TokenSessionManager tokenSessionManager,HashedCredentialsMatcher hashedCredentialsMatcher){
        DefaultWebSecurityManager defaultWebSecurityManager = new DefaultWebSecurityManager();
        // 设置realm
        defaultWebSecurityManager.setRealm(realm);
        defaultWebSecurityManager.setRememberMeManager(new CookieRememberMeManager());
        // realm 密码验证器
        realm.setCredentialsMatcher(hashedCredentialsMatcher);
        // 设置sessionManager
//        defaultWebSecurityManager.setSessionManager(tokenSessionManager);
        return defaultWebSecurityManager;
    }


    // 自定义token获取
    @Bean
    public TokenSessionManager tokenSessionManager(RedisSessionDAO sessionDAO){
        TokenSessionManager tokenSessionManager = new TokenSessionManager();
        tokenSessionManager.setSessionDAO(sessionDAO);
        return tokenSessionManager;
    }

    /**
     * 验证信息存入redis
     * @author     xinchuang
     * @param redisManager :
     * @return : org.apache.shiro.session.mgt.eis.SessionDAO
     */
    @Bean
    public RedisSessionDAO sessionDAO(RedisManager redisManager){
        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
        redisSessionDAO.setRedisManager(redisManager);
        return redisSessionDAO;
    }

    @Bean
    public RedisManager redisManager(){
        RedisManager redisManager = new RedisManager();
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
//         最大连接数
        jedisPoolConfig.setMaxTotal(20);
        jedisPoolConfig.setMaxIdle(3);
        jedisPoolConfig.setMinIdle(2);
        redisManager.setJedisPoolConfig(jedisPoolConfig);
        redisManager.setHost("117.50.22.58:6379");
        redisManager.setDatabase(13);
        redisManager.setPassword("xinchuang");
//        redisManager.setTimeout();
        return redisManager;
    }

    /**
     * 放行与拦截 配置shiro的过滤器
     * @author     xinchuang
     * @return : org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition
     */
    @Bean(value = "shiroFilterBean")
    public ShiroFilterFactoryBean shiroFilterFactoryBean(DefaultWebSecurityManager securityManager) {
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        // 设置安全管理器
        factoryBean.setSecurityManager(securityManager);
        // 设置放行的路径
        Map<String,String> filterMap = new LinkedHashMap<String,String>();
        // 设置未登陆的时要跳转的页面
        factoryBean.setLoginUrl("/index/toLogin");
        // 认证成功跳转页面
        factoryBean.setSuccessUrl("/index/toIndex");
        // 配置未授权提示页面
        factoryBean.setUnauthorizedUrl("/404");
        filterMap.put("/static/**","anon");
        filterMap.put("/css/**","anon");
        filterMap.put("/http/**","anon");
        filterMap.put("/images/**","anon");
        filterMap.put("/js/**","anon");
        filterMap.put("/json/**","anon");
        filterMap.put("/layui/**","anon");
        filterMap.put("/layui_ext/**","anon");
        filterMap.put("/plugins/**","anon");
        filterMap.put("/index/toLogin","anon");
        filterMap.put("/login/login","anon");
        filterMap.put("/login/getCaptcha","anon");
        filterMap.put("/logout","logout");
        filterMap.put("/**","authc");
        factoryBean.setFilterChainDefinitionMap(filterMap);
        return factoryBean;
    }

    /**
     * 注册shiro的委托过滤器，相当于之前在web.xml里面配置的
     * @return
     */
    @Bean
    public FilterRegistrationBean<DelegatingFilterProxy> delegatingFilterProxy() {
        FilterRegistrationBean<DelegatingFilterProxy> filterRegistrationBean = new FilterRegistrationBean<DelegatingFilterProxy>();
        DelegatingFilterProxy proxy = new DelegatingFilterProxy();
        proxy.setTargetFilterLifecycle(true);
        proxy.setTargetBeanName("shiroFilterBean");
        filterRegistrationBean.setFilter(proxy);
        return filterRegistrationBean;
    }


    // 注解的权限验证
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(DefaultWebSecurityManager defaultWebSecurityManager){
        AuthorizationAttributeSourceAdvisor a = new AuthorizationAttributeSourceAdvisor();
        a.setSecurityManager(defaultWebSecurityManager);
        return a;
    }

    @Bean
    public DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }

    @Bean(name = "shiroDialect")
    public ShiroDialect shiroDialect() {
        return new ShiroDialect();
    }


    // 密码适配器
    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher(){
        // 指定算法
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher("MD5");
        // 散列次数
        hashedCredentialsMatcher.setHashIterations(2);
        hashedCredentialsMatcher.setStoredCredentialsHexEncoded(true);
        return hashedCredentialsMatcher;
    }
}
