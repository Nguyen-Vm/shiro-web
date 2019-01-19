package com.ostay.shiroweb.controller;

import com.ostay.shiroweb.mapper.UserMapper;
import com.ostay.shiroweb.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/list")
    public ModelAndView userInfo(Model model) {
        model.addAttribute("title", "用户管理");
        model.addAttribute("userList", userList());
        return new ModelAndView("user/list", "userModel", model);
    }

    @GetMapping("/detail/{id}")
    public ModelAndView detail(@PathVariable long id, Model model) {
        User user = getById(id);
        model.addAttribute("title", "用户详情");
        model.addAttribute("user", user);
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
        userMapper.save(user);
        return new ModelAndView("redirect:../user/list");
    }

    @GetMapping("/delete/{id}")
    public ModelAndView delete(@PathVariable long id) {
        userMapper.deleteById(id);
        return new ModelAndView("redirect:../../user/list");
    }

    @GetMapping("/modify/{id}")
    public ModelAndView modify(@PathVariable long id, Model model) {
        User user = getById(id);
        model.addAttribute("user", user);
        model.addAttribute("title", "修改用户");
        return new ModelAndView("user/form", "userModel", model);
    }

    private List<User> userList() {
        return userMapper.findAll();
    }

    private User getById(long id) {
        return userMapper.findById(id).get();
    }

}
