package org.example.coursedesign.service.impl;

import org.example.coursedesign.mapper.ProductMapper;
import org.example.coursedesign.pojo.PageInfo;
import org.example.coursedesign.pojo.ViewTime;
import org.example.coursedesign.pojo.categories;
import org.example.coursedesign.pojo.Product;
import org.example.coursedesign.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    ProductMapper productMapper;

    public List<categories> getCategories(){
        return productMapper.getCategories();
    }

    public PageInfo<Product> getProducts(int page, int pageSize, Integer categoryId, String keyword){
        // 计算偏移量
        int offset = (page - 1) * pageSize;

        // 查询当前页数据
        List<Product> products = productMapper.selectProducts(
                offset, pageSize, categoryId, keyword
        );

        // 查询总数
        int total = productMapper.countProducts(categoryId, keyword);

        // 封装分页结果
        return new PageInfo<>(page, pageSize, total, products);

    }

    @Override
    public void insertViewTime(String username, String viewTime, String product_name) {
        productMapper.insertViewTime(username, viewTime, product_name);
    }

    @Override
    public PageInfo<ViewTime> getViewTimes(String username, int page, int pageSize) {
        // 计算偏移量
        int offset = (page - 1) * pageSize;

        // 查询当前页数据
        List<ViewTime> viewTimes = productMapper.getViewTime(username, offset, pageSize);

        // 查询总数
        int total = productMapper.getViewTimeCount(username);

        // 封装分页结果
        return new PageInfo<>(page, pageSize, total, viewTimes);
    }
}
