package com.example.webshoporder.services.impl;

import com.example.webshoporder.models.BuyOrder;
import com.example.webshoporder.models.Item;
import com.example.webshoporder.models.Order;
import com.example.webshoporder.models.TestOrder;
import com.example.webshoporder.repositories.OrderRepository;
import com.example.webshoporder.services.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class OrderServiceImpl implements OrderService {

    private final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;
    private final ThreadPoolTaskExecutor executor;

    @Value("${customer-service.url}")
    String customerUrl;

    @Value("${item-service.url}")
    String itemUrl;

    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
        this.restTemplate = new RestTemplate()
        this.executor = new ThreadPoolTaskExecutor();
        this.executor.initialize();
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public ResponseEntity<Object> getAllOrdersWithItems() {
        List<Order> orders = orderRepository.findAll();
        List<TestOrder> ordersList = new ArrayList<>();
        for (Order order : orders) {
            List<Item> items = fillOrders(order);
            ordersList.add(new TestOrder(order.getId(), order.getCustomerId(), items, order.getOrderDate()));
        }
        return new ResponseEntity<>(ordersList, HttpStatus.OK);
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
    public ResponseEntity<Object> getOrderByIdWithItems(long id){
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) {
            return new ResponseEntity<>(Collections.singletonMap("error", "Order not found"), HttpStatus.NOT_FOUND);
        } else {
            List<Item> items = fillOrders(order);
            return new ResponseEntity<>(new TestOrder(order.getId(), order.getCustomerId(), items, order.getOrderDate()), HttpStatus.OK);
        }
    }
    @Override
    public ResponseEntity<Object> buyItem(BuyOrder buyOrder) {
        try {
            return executor.submit(() -> methodWithTimeout(buyOrder)).get(3, TimeUnit.SECONDS);
        } catch (Exception e) {
            return new ResponseEntity<>(Collections.singletonMap("error", "Timeout or exception occurred"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> methodWithTimeout(BuyOrder buyOrder) {
        boolean customerExists = checkCustomerExistence(buyOrder.getCustomerId());
        logger.info("Customer exists: " + customerExists);
        if (!customerExists) {
            logger.info("Customer not found, sending back error message");
            return new ResponseEntity<>(Collections.singletonMap("error", "Customer not found, with customerId: " + buyOrder.getCustomerId()), HttpStatus.NOT_FOUND);
        } else {
            List<Item> itemList = new ArrayList<>();
            List<Long> items = buyOrder.getItemIds();
            List<String> notFoundItems = new ArrayList<>();
            for (Long item : items) {
                Item itemFromDb = getItem(item);

                if (itemFromDb == null) {
                    notFoundItems.add("Product not found with id: " + item);
                } else {
                    itemList.add(itemFromDb);
                }
            }
            if (!notFoundItems.isEmpty()) {
                return new ResponseEntity<>(Collections.singletonMap("error", notFoundItems), HttpStatus.NOT_FOUND);
            }

            Order order = new Order(buyOrder.getCustomerId(), buyOrder.getItemIds());
            orderRepository.save(order);
            TestOrder testOrder = new TestOrder(order.getId(), buyOrder.getCustomerId(), itemList, order.getOrderDate());
            logger.info("Order created: " + order);
            logger.info("Order sent back to customer: " + testOrder);
            return new ResponseEntity<>(testOrder, HttpStatus.CREATED);
        }
    }


    @Retryable(retryFor = RestClientException.class, backoff = @Backoff(delay = 1000, maxDelay = 3000))
    private boolean checkCustomerExistence(long customerId) {
        try {
            ResponseEntity<Boolean> responseEntity = restTemplate.exchange(customerUrl + customerId, HttpMethod.GET, null, Boolean.class);
            return Boolean.TRUE.equals(responseEntity.getBody());
        } catch (HttpClientErrorException.NotFound ex) {
            return false;
        } catch (RestClientException e) {
            return false;
        }
    }

    @Retryable(retryFor = RestClientException.class, backoff = @Backoff(delay = 1000, maxDelay = 3000))
    private Item getItem(long itemId) {
        try {
            ResponseEntity<Item> responseEntity = restTemplate.exchange(itemUrl + itemId, HttpMethod.GET, null, Item.class);
            return responseEntity.getBody();
        } catch (RestClientException ex) {
            return null;
        }
    }

    private List<Item> fillOrders(Order order) {
        List<Item> items = new ArrayList<>();
        for (Long itemId : order.getItems()) {
            Item item = getItem(itemId);
            if (item != null) {
                items.add(item);
            }
        }
        return items;
    }
}