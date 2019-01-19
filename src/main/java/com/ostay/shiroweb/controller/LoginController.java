package com.ostay.shiroweb.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class LoginController {

    @GetMapping({"/", "/index"})
    public String get() {
        return "/index";
    }

    @RequestMapping(value = "/login", method = {RequestMethod.GET, RequestMethod.POST})
    public void login(HttpServletRequest request, Map<String, Object> map) {
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            return;
        }
        String exception = (String) request.getAttribute("shiroLoginFailure");
        exception = exception == null ? "" : exception;
        String msg;
        if (UnknownAccountException.class.getName().equals(exception)) {
            msg = "账号不存在";
        } else if (IncorrectCredentialsException.class.getName().equals(exception)) {
            msg = "密码不正确";
        } else {
            msg = exception;
        }
        map.put("msg", msg);
    }

    @GetMapping("/403")
    @ResponseBody
    public String unauthorizedRole(){
        return "你没有该操作权限";
    }

    @GetMapping("/logout")
    public String login() {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return "login";
    }

}
