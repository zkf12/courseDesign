package org.example.coursedesign.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PageInfo<T> {
    private int page;
    private int pageSize;
    private int total;
    private List<T> list;
}
