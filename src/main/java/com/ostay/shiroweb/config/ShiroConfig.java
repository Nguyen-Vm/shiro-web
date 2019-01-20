package com.ostay.shiroweb.config;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.AbstractValidatingSessionManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {

    // 登录后session缓存时长(-1关闭浏览器后失效)
    private final int SESSION_TIMEOUT_SECOND = -1; //5 * 60 * 60;

    // session 在缓存工具中的时长
    private final int SESSION_CACHE_TIMEOUT_SECOND = 6 * 60 * 60;

    @Autowired
    private Environment environment;

    @Bean
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        Map<String, String> filterChainDefinitionMap = new HashMap<>();
        filterChainDefinitionMap.put("/logout", "logout");
        filterChainDefinitionMap.put("/order/**", "authc, roles[admin]");
        filterChainDefinitionMap.put("/**", "authc");

        shiroFilterFactoryBean.setLoginUrl("/login");
        shiroFilterFactoryBean.setSuccessUrl("/index");
        shiroFilterFactoryBean.setUnauthorizedUrl("/403");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }

    @Bean
    public SecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(shiroRealm());
        securityManager.setSessionManager(sessionManager());
        return securityManager;
    }

    @Bean
    public MyShiroRealm shiroRealm() {
        return new MyShiroRealm();
    }

    @Bean
    public DefaultWebSessionManager sessionManager() {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();

        // 是否启用cookie
        sessionManager.setSessionIdCookieEnabled(true);
        SimpleCookie simpleCookie = new SimpleCookie("SHIRO_JSESSIONID");
        simpleCookie.setPath("/");
        simpleCookie.setMaxAge(SESSION_TIMEOUT_SECOND);
        simpleCookie.setHttpOnly(false); // false支持跨域
        sessionManager.setSessionIdCookie(simpleCookie);

        sessionManager.setSessionDAO(redisSessionDAO());

        //设置session过期时间,默认为30分钟
        sessionManager.setGlobalSessionTimeout(SESSION_TIMEOUT_SECOND * 1000);

        // 是否在会话过期后会调用SessionDAO的delete方法删除会话 默认true
        sessionManager.setDeleteInvalidSessions(true);
        // 定时检查失效的session
        sessionManager.setSessionValidationSchedulerEnabled(true);
        // 会话验证器调度时间,默认1小时
        sessionManager.setSessionValidationInterval(AbstractValidatingSessionManager.DEFAULT_SESSION_VALIDATION_INTERVAL);

        sessionManager.setSessionIdUrlRewritingEnabled(true);
        return sessionManager;
    }

    @Bean
    public RedisSessionDAO redisSessionDAO() {
        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
        redisSessionDAO.setRedisManager(redisManager());
        redisSessionDAO.setKeyPrefix("SHIRO:SESSION:");
        redisSessionDAO.setExpire(SESSION_CACHE_TIMEOUT_SECOND); // session 在redis中缓存时长 Please make sure expire is longer than session.getTimeout()
        return redisSessionDAO;
    }

    @Bean
    public RedisManager redisManager() {
        RedisManager redisManager = new RedisManager();
        redisManager.setHost(environment.getProperty("redis.host"));
        redisManager.setPort(environment.getProperty("redis.port", Integer.class));
        String password = environment.getProperty("redis.password");
        if (!StringUtils.isEmpty(password)) {
            redisManager.setPassword(password);
        }
        return redisManager;
    }


}
