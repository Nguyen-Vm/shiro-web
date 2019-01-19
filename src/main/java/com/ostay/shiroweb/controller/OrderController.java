package com.ostay.shiroweb.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @GetMapping("/list")
    public ModelAndView list(Model model) {
        List<String> orderList = new ArrayList<>();
        orderList.add("1");
        orderList.add("2");
        orderList.add("3");
        model.addAttribute("title", "订单管理");
        model.addAttribute("orderList", orderList);
        return new ModelAndView("order/list", "orderModel", model);
    }
}
