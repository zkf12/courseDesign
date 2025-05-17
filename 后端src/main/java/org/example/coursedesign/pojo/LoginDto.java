package org.example.coursedesign.pojo;

import lombok.Data;

@Data
public class LoginDto {
    private String username;
    private String password;
    private String confirmPassword;
}