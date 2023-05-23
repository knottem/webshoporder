package com.example.webshoporder.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class TestOrder {

    private long id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderDate;
    private long customerId;
    private List<Item> items;

    public TestOrder(Long id, long customerId, List<Item> items, LocalDateTime orderDate){
        this.id = id;
        this.customerId = customerId;
        this.items = items;
        this.orderDate = orderDate;
    }

}