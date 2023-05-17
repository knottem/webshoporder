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


    /*
    @PostMapping("/orders/buy")
    public ResponseEntity<Object> buyItem(@RequestBody BuyOrder buyOrder) {
        //get from customer api
        Customer customer = customerRepository.findById(buyOrder.getCustomerId()).isPresent() ? customerRepository.findById(buyOrder.getCustomerId()).get() : null;
        if (customer == null) {
            return new ResponseEntity<>(Collections.singletonMap("error", "Customer not found"), HttpStatus.NOT_FOUND);
        } else {
            List <Item> itemList = new ArrayList<>();
            List<Long> items = buyOrder.getItemIds();
            for (Long item : items) {
                //Item itemFromDb = get from item api
                Item itemFromDb = itemRepository.findById(item).isPresent() ? itemRepository.findById(item).get() : null;
                if (itemFromDb == null) {
                    return new ResponseEntity<>(Collections.singletonMap("error", "Product not found"), HttpStatus.NOT_FOUND);
                } else {
                    itemList.add(itemFromDb);
                }
            }
            Order order = new Order(customer, itemList);
            orderRepository.save(order);
            logger.info("Order created: " + order);
            return new ResponseEntity<>(order, HttpStatus.CREATED);
        }
    }

 */
}