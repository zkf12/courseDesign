package org.example.coursedesign.service;

import org.example.coursedesign.pojo.PageInfo;
import org.example.coursedesign.pojo.ViewTime;
import org.example.coursedesign.pojo.categories;
import org.example.coursedesign.pojo.Product;

import java.util.List;

public interface ProductService {
    public List<categories> getCategories();
    public PageInfo<Product> getProducts(int page, int pageSize, Integer categoryId, String keyword);
    void insertViewTime(String username, String viewTime,String product_name);
    PageInfo<ViewTime> getViewTimes(String username,int page, int pageSize);
}
