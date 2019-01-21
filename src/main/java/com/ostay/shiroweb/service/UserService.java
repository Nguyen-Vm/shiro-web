package com.ostay.shiroweb.service;

import com.ostay.shiroweb.dto.request.UserLoginReq;
import com.ostay.shiroweb.dto.response.UserLoginResp;
import com.ostay.shiroweb.mapper.UserMapper;
import com.ostay.shiroweb.model.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public UserLoginResp login(UserLoginReq req) {
        User user = userMapper.findByName(req.getName());
        if (user == null || !user.getPassword().equals(req.getPassword())) {
            return null;
        }
        return covert(user);
    }

    private UserLoginResp covert(User user) {
        UserLoginResp resp = new UserLoginResp();
        BeanUtils.copyProperties(user, resp);
        return resp;
    }

    public List<User> userList() {
        return userMapper.findAll();
    }

    public User getById(long id) {
        return userMapper.findById(id).get();
    }


    public void save(User user) {
        userMapper.save(user);
    }

    public void remove(long id) {
        userMapper.deleteById(id);
    }
}
