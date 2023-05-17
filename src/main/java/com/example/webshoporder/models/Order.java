package com.example.webshoporder.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "orders")
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue
    private long id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderDate;
    private long customerId;
    @ElementCollection
    @CollectionTable(name = "orders_items", joinColumns = @JoinColumn(name = "order_id"))
    @Column(name = "items_id")
    private List<Long> items;

    public Order(long customerId, List<Long> items){
        this.customerId = customerId;
        this.items = items;
        this.orderDate = LocalDateTime.now().withNano(0);
    }

}