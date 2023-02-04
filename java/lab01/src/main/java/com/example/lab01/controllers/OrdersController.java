package com.example.lab01.controllers;

import com.example.lab01.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@AllArgsConstructor
public class OrdersController {

    private final OrderService orderService;

    @GetMapping
    String getOrder(){
        orderService.publish();
        return "ok";
    }

}
