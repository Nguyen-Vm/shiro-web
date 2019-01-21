package com.ostay.shiroweb.config;

import com.ostay.shiroweb.dto.request.UserLoginReq;
import com.ostay.shiroweb.dto.response.UserLoginResp;
import com.ostay.shiroweb.mapper.RoleToUserMapper;
import com.ostay.shiroweb.service.UserService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

public class MyShiroRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleToUserMapper roleToUserMapper;

    /**
     * 认证
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        UserLoginResp resp = userService.login(new UserLoginReq(token.getUsername(), new String(token.getPassword())));
        if (resp == null) {
            return null;
        }
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(resp, token.getPassword(), getName());
        return authenticationInfo;
    }

    /**
     * 授权
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        UserLoginResp user = (UserLoginResp) principalCollection.getPrimaryPrincipal();
        Set<String> roles = getRolesByUsername(user.getName());
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        authorizationInfo.setRoles(roles);
        return authorizationInfo;
    }

    private Set<String> getRolesByUsername(String username) {
        return roleToUserMapper.findByUsername(username);
    }

}
