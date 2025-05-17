package org.example.coursedesign.pojo;

import lombok.Data;

import java.sql.Timestamp;
import java.time.Duration;

@Data
public class ViewTime {
    private int id;
    private String username;
    private String time;
    private Timestamp viewTime;
    private String productName;
}
