package org.example.coursedesign.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Cart {
    private int id;
    private int userId;
    private int productId;
    private LocalDateTime addTime;
}
