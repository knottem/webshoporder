package com.example.webshoporder.controllers;

import com.example.webshoporder.models.BuyOrder;
import com.example.webshoporder.models.Order;
import com.example.webshoporder.services.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping({"/orders", "/orders/"})
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/ordersitems")
    public ResponseEntity<Object> getAllOrdersWithItems() {
        return orderService.getAllOrdersWithItems();
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<Object> getOrderById(@PathVariable long id) {
        return orderService.getOrderById(id);
    }

    @GetMapping("/ordersitems/{id}")
    public ResponseEntity<Object> getOrderByIdWithItems(@PathVariable long id) {
        return orderService.getOrderByIdWithItems(id);
    }

    @PostMapping("/orders/buy")
    public ResponseEntity<Object> buyItem(@RequestBody BuyOrder buyOrder) {
        return orderService.buyItem(buyOrder);
    }
}