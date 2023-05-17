package com.example.webshoporder.controllers;

import com.example.webshoporder.models.BuyOrder;
import com.example.webshoporder.models.Order;
import com.example.webshoporder.repositories.OrderRepository;
import com.example.webshoporder.services.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @RequestMapping({"/orders", "/orders/"})
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @RequestMapping("/orders/{id}")
    public ResponseEntity<Object> getOrderById(@PathVariable(required = false) long id) {
        return orderService.getOrderById(id);
    }

    @PostMapping("/orders/buy")
    public ResponseEntity<Object> buyItem(@RequestBody BuyOrder buyOrder) {
        return orderService.buyItem(buyOrder);
    }
}