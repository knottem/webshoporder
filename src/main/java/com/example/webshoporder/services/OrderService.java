package com.example.webshoporder.services;

import com.example.webshoporder.models.BuyOrder;
import com.example.webshoporder.models.Order;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface OrderService {
    List<Order> getAllOrders();
    ResponseEntity<Object> getAllOrdersWithItems();
    ResponseEntity<Object> getOrderById(long id);
    ResponseEntity<Object> getOrderByIdWithItems(long id);
    ResponseEntity<Object> buyItem(BuyOrder buyOrder);
}