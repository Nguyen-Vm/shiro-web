package com.ostay.shiroweb.dto.request;

import java.io.Serializable;

public class UserLoginReq implements Serializable {

    private String name;

    private String password;

    public UserLoginReq() {
    }

    public UserLoginReq(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
