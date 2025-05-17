package org.example.coursedesign.pojo;

import lombok.Data;

@Data
public class OrderDetail {
    private int id;
    private int userId;
    private int productId;
    private int orderId;
    private Integer quantity;
}