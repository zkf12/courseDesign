package org.example.coursedesign.pojo;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class PurchaseHistory {
    private int orderId;
    private String username;
    private double totalPrice;
    private Timestamp tradeTime;
}
