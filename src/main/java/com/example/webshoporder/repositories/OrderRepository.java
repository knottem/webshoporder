package com.example.webshoporder.repositories;

import com.example.webshoporder.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> { }