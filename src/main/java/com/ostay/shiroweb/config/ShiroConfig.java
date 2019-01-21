package com.ostay.shiroweb.config;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.AbstractValidatingSessionManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisCacheManager;
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

    // 登录后 session 缓存时长(-1关闭浏览器后失效，设置“记住我”后，关闭浏览器不失效)
    private final int SESSION_TIMEOUT_SECOND = -1; //5 * 60 * 60;

    // session 在缓存工具中的时长
    private final int SESSION_CACHE_TIMEOUT_SECOND = 6 * 60 * 60;

    // 用户的授权信息缓存时长
    private final int AUTHZ_TIMEOUT_SECOND = 1 * 15 * 60;

    // remember me cookie timeout
    private final int REMEMBER_ME_COOKIE_TIMEOUT_SECOND = 1 * 24 * 60 * 60;

    @Autowired
    private Environment environment;

    @Bean
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        Map<String, String> filterChainDefinitionMap = new HashMap<>();
        filterChainDefinitionMap.put("/logout", "logout");

        filterChainDefinitionMap.put("/resources/**", "anon");
        filterChainDefinitionMap.put("/login", "anon");

        filterChainDefinitionMap.put("/403", "authc, roles[user]");
        filterChainDefinitionMap.put("/**", "authc, roles[admin]");
        // 设置后，记住登录才生效
        filterChainDefinitionMap.put("/**", "user, roles[admin]");

        shiroFilterFactoryBean.setLoginUrl("/login");
        shiroFilterFactoryBean.setSuccessUrl("/");
        shiroFilterFactoryBean.setUnauthorizedUrl("/403");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }

    @Bean
    public SecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(shiroRealm());
        securityManager.setSessionManager(sessionManager());
        securityManager.setCacheManager(redisCacheManager());
        securityManager.setRememberMeManager(rememberMeManager());
        return securityManager;
    }

    @Bean
    public RedisCacheManager redisCacheManager() {
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        redisCacheManager.setRedisManager(redisManager());
        redisCacheManager.setKeyPrefix("SHIRO:CACHE:");
        redisCacheManager.setExpire(AUTHZ_TIMEOUT_SECOND); // 权限信息缓存时长(s), 默认30分
        return redisCacheManager;
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

        // 设置 session 过期时间,默认为30分钟
        // 为负数时表示永不超时，回话结束后失效，即浏览器关闭
        // 单位是ms，转成s后为负数需要不大于-1000
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
        redisManager.setHost(environment.getProperty("spring.redis.host"));
        redisManager.setPort(environment.getProperty("spring.redis.port", Integer.class));
        String password = environment.getProperty("spring.redis.password");
        if (!StringUtils.isEmpty(password)) {
            redisManager.setPassword(password);
        }
        return redisManager;
    }

    /**
     * 修改COOKIE属性
     * @return
     */
    @Bean
    public CookieRememberMeManager rememberMeManager() {
        CookieRememberMeManager rememberMeManager = new CookieRememberMeManager();

        SimpleCookie cookie = new SimpleCookie("REMEMBER_COOKIE");
        cookie.setPath("/");
        cookie.setMaxAge(REMEMBER_ME_COOKIE_TIMEOUT_SECOND);
        cookie.setHttpOnly(false);

        rememberMeManager.setCookie(cookie);
        return rememberMeManager;
    }

}
