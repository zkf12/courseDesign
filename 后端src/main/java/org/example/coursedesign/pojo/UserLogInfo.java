package org.example.coursedesign.pojo;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class UserLogInfo {
    private int id;
    private String username;
    private Timestamp time;
    private String ip;
    private String loginOrLogout;
}
