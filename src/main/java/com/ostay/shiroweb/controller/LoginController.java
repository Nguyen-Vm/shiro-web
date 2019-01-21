package com.ostay.shiroweb.controller;

import com.ostay.shiroweb.dto.request.UserLoginReq;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class LoginController {

    @GetMapping({"/", "/index"})
    public String get() {
        return "/index";
    }

    @RequestMapping(value = "/login", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView login(UserLoginReq req, HttpServletRequest request, Map<String, Object> map) {
        if (request.getMethod().equals("GET")) {
            return new ModelAndView("login");
        }
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            try {
                // TOKEN构造器传入rememberMe，真正决定是否添加“记住我”的COOKIE
                UsernamePasswordToken token = new UsernamePasswordToken(req.getName(), req.getPassword(), req.isRememberMe());
                subject.login(token);
                return new ModelAndView("index");
            } catch (Exception e) {
                if (e instanceof UnknownAccountException || e instanceof IncorrectCredentialsException) {
                    map.put("msg", "账号或密码不正确！");
                }
            }
        }
        return new ModelAndView("login");
    }

    @GetMapping("/403")
    public ModelAndView unauthorizedRole(){
        return new ModelAndView("403");
    }

    @GetMapping("/logout")
    public ModelAndView login() {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return new ModelAndView("login");
    }

}
