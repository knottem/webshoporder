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

    @RequestMapping({"/orders", "/orders/"})
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @RequestMapping("/ordersitems")
    public ResponseEntity<Object> getAllOrdersWithItems() {
        return orderService.getAllOrdersWithItems();
    }

    @RequestMapping("/orders/{id}")
    public ResponseEntity<Object> getOrderById(@PathVariable long id) {
        return orderService.getOrderById(id);
    }

    @RequestMapping("/ordersitems/{id}")
    public ResponseEntity<Object> getOrderByIdWithItems(@PathVariable long id) {
        return orderService.getOrderByIdWithItems(id);
    }

    @PostMapping("/orders/buy")
    public ResponseEntity<Object> buyItem(@RequestBody BuyOrder buyOrder) {
        return orderService.buyItem(buyOrder);
    }
}