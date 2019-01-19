package com.ostay.shiroweb.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @GetMapping("/list")
    public List<String> list() {
        List<String> orderList = new ArrayList<>();
        orderList.add("1");
        orderList.add("2");
        orderList.add("3");
        return orderList;
    }
}
