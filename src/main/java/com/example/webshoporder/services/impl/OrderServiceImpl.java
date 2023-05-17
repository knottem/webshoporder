package com.example.webshoporder.services.impl;

import com.example.webshoporder.models.BuyOrder;
import com.example.webshoporder.models.Order;
import com.example.webshoporder.repositories.OrderRepository;
import com.example.webshoporder.services.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
    private final OrderRepository orderRepository;

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
        long customerId = buyOrder.getCustomerId();
        List<Long> items = buyOrder.getItemIds();
        if (customerId == 0L) {
            return new ResponseEntity<>(Collections.singletonMap("error", "Customer not found, please add: customerId:"), HttpStatus.NOT_FOUND);
        } else if (items == null){
            return new ResponseEntity<>(Collections.singletonMap("error", "Items List not found, please add: itemIds:" ), HttpStatus.NOT_FOUND);
        }   else if (items.isEmpty()) {
            return new ResponseEntity<>(Collections.singletonMap("error", "Items not found"), HttpStatus.NOT_FOUND);
        } else {
            Order order = new Order(customerId, buyOrder.getItemIds());
            orderRepository.save(order);
            logger.info("Order created: " + order);
            return new ResponseEntity<>(order, HttpStatus.CREATED);
        }
    }



    /*
    @Override
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