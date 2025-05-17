package org.example.coursedesign.pojo;

import lombok.Data;

@Data
public class Product {
    private int id;
    private String name;
    private double price;
    private String image;
    private String description;
    private String category;
    private String shop;
    private int stock;
    private String status;
}
