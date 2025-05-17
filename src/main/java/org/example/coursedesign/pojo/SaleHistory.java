package org.example.coursedesign.pojo;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class SaleHistory {
    private int id;
    private String username;
    private String productName;
    private int quantity;
    private Timestamp tradeTime;
    private double totalPrice;
}
