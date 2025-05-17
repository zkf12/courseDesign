package org.example.coursedesign.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    private int id;
    private String username;
    private String password;
    private String nickname;
    private int permission;
    private String email;
    private String picture;
    private String phone;
    private String shop;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
