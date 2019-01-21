package com.ostay.shiroweb.controller;

import com.ostay.shiroweb.config.MyShiroRealm;
import com.ostay.shiroweb.model.User;
import com.ostay.shiroweb.service.UserService;
import org.crazycake.shiro.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private MyShiroRealm myShiroRealm;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @GetMapping("/list")
    public ModelAndView userInfo(Model model) {
        model.addAttribute("title", "用户管理");
        model.addAttribute("userList", userService.userList());
        return new ModelAndView("user/list", "userModel", model);
    }

    @GetMapping("/detail/{id}")
    public ModelAndView detail(@PathVariable long id, Model model) {
        model.addAttribute("title", "用户详情");
        model.addAttribute("user", userService.getById(id));
        return new ModelAndView("user/view", "userModel", model);
    }

    @GetMapping("/add")
    public ModelAndView add(Model model) {
        model.addAttribute("title", "创建用户");
        model.addAttribute("user", new User());
        return new ModelAndView("user/form", "userModel", model);
    }

    @PostMapping("/save")
    public ModelAndView saveUser(User user) {
        userService.save(user);
        return new ModelAndView("redirect:../user/list");
    }

    @GetMapping("/delete/{id}")
    public ModelAndView delete(@PathVariable long id) {
        userService.remove(id);
        removeRedisCache(id);
        return new ModelAndView("redirect:../../user/list");
    }

    @GetMapping("/modify/{id}")
    public ModelAndView modify(@PathVariable long id, Model model) {
        model.addAttribute("user", userService.getById(id));
        model.addAttribute("title", "修改用户");
        removeRedisCache(id);
        return new ModelAndView("user/form", "userModel", model);
    }

    private void removeRedisCache(long id) {
        RedisCache redisCache = (RedisCache) myShiroRealm.getAuthorizationCache();
        String redisCacheKeyPrefix = redisCache.getKeyPrefix();
        StringBuilder sb = new StringBuilder();
        sb.append(redisCacheKeyPrefix).append(id);
        redisTemplate.boundValueOps(sb.toString()).expire(1, TimeUnit.SECONDS);
    }
}
