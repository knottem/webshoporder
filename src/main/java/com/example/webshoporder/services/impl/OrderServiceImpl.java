package com.example.webshoporder.services.impl;

import com.example.webshoporder.models.BuyOrder;
import com.example.webshoporder.models.Item;
import com.example.webshoporder.models.Order;
import com.example.webshoporder.repositories.OrderRepository;
import com.example.webshoporder.services.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
    private final OrderRepository orderRepository;

    RestTemplate restTemplate = new RestTemplate();

    @Value("${customer-service.url}")
    String customerUrl;

    @Value("${item-service.url}")
    String itemUrl;

    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public ResponseEntity<Object> getOrderById(long id) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) {
            return new ResponseEntity<>(Collections.singletonMap("error", "Order not found"), HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(order, HttpStatus.OK);
        }
    }

    @Override
    public ResponseEntity<Object> buyItem(BuyOrder buyOrder) {
        List<Long> items = buyOrder.getItemIds();
        boolean customerExists = checkCustomerExistence(buyOrder.getCustomerId());
        logger.info("Customer exists: " + customerExists);
        if (!customerExists) {
            logger.info("Customer not found, sending back error message");
            return new ResponseEntity<>(Collections.singletonMap("error", "Customer not found, please add: customerId:"), HttpStatus.NOT_FOUND);
        } else if (items == null){
            return new ResponseEntity<>(Collections.singletonMap("error", "Items List not found, please add: itemIds:" ), HttpStatus.NOT_FOUND);
        } else if (items.isEmpty()) {
            return new ResponseEntity<>(Collections.singletonMap("error", "Items not found"), HttpStatus.NOT_FOUND);
        } else {
            Order order = new Order(buyOrder.getCustomerId(), buyOrder.getItemIds());
            orderRepository.save(order);
            logger.info("Order created: " + order);
            return new ResponseEntity<>(order, HttpStatus.CREATED);
        }
    }

    public ResponseEntity<Object> buyItemV2(BuyOrder buyOrder) {
        boolean customerExists = checkCustomerExistence(buyOrder.getCustomerId());
        logger.info("Customer exists: " + customerExists);
        if (!customerExists) {
            logger.info("Customer not found, sending back error message");
            return new ResponseEntity<>(Collections.singletonMap("error", "Customer not found, please add: customerId:"), HttpStatus.NOT_FOUND);
        } else {
            List <Item> itemList = new ArrayList<>();
            List<Long> items = buyOrder.getItemIds();
            for (Long item : items) {
                Item itemFromDb = getItem(item);
                if (itemFromDb == null) {
                    return new ResponseEntity<>(Collections.singletonMap("error", "Product not found"), HttpStatus.NOT_FOUND);
                } else {
                    itemList.add(itemFromDb);
                }
            }
            Order order = new Order(buyOrder.getCustomerId(), buyOrder.getItemIds());
            orderRepository.save(order);
            logger.info("Order items = " + itemList);
            logger.info("Order created: " + order);
            return new ResponseEntity<>(order, HttpStatus.CREATED);
        }
    }
    private boolean checkCustomerExistence(long customerId) {
        try {
            ResponseEntity<Boolean> responseEntity = restTemplate.exchange(customerUrl + customerId, HttpMethod.GET, null, Boolean.class);
            return responseEntity.getBody();
        } catch (HttpClientErrorException.NotFound ex) {
            return false;
        }
    }

    private Item getItem(long itemId) {
        try {
            ResponseEntity<Item> responseEntity = restTemplate.exchange(itemUrl + itemId, HttpMethod.GET, null, Item.class);
            return responseEntity.getBody();
        } catch (HttpClientErrorException.NotFound ex) {
            return null;
        }
    }

}