package org.example.coursedesign.controller;


import org.example.coursedesign.pojo.*;
import org.example.coursedesign.service.ProductService;

import org.example.coursedesign.service.RecommendationService;
import org.example.coursedesign.service.UserService;
import org.example.coursedesign.utils.ThreadlocalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.example.coursedesign.AppConstants.*;

@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private RecommendationService recommendationService;

    @GetMapping("/categories")
    public Result getCategories() {
        List<categories> rawCategories = productService.getCategories();
        if (rawCategories.isEmpty()) {
            return Result.error(GET_CATEGORIES_ERROR, "查询分类失败");
        } else {
            List<Map<String, String>> formattedCategories = new ArrayList<>();
            for (int i = 0; i < rawCategories.size(); i++) {
                Map<String, String> categoryMap = new HashMap<>();
                categoryMap.put("id", String.valueOf(rawCategories.get(i).getId())); // 从1开始编号
                categoryMap.put("name", rawCategories.get(i).getCategory());
                formattedCategories.add(categoryMap);
            }

            Map<String, Object> data = new HashMap<>();
            data.put("categories", formattedCategories);
            return Result.success(GET_CATEGORIES_SUCCESS, "查询分类成功", data);
        }
    }

    @GetMapping("/getProduct")
    public Result getProduct (
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int pageSize,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword){

            // 处理分类参数（"all"转为null）
            Integer categoryId = "all".equals(category) ? null : Integer.parseInt(category);

            // 调用Service
            PageInfo<Product> pageInfo = productService.getProducts(
                    page, pageSize, categoryId, keyword
            );


            if(pageInfo.getTotal() == 0){
                return Result.error(NO_SUCH_PRODUCT_ERROR,"没有该商品");
            }

            // 返回统一结构
            return Result.success(GET_PRODUCT_SUCCESS,"物品获取成功",pageInfo);
    }

    @GetMapping("/for-user")
    public Result recommendForUser(
            @RequestParam(defaultValue = "10") int limit) {
        Map<String, Object> u = ThreadlocalUtils.get();
        int userId = (int) u.get("id");
        String username = (String) u.get("username");

        List<Product> recommendations = recommendationService.recommendForUser(userId, username, limit);
        return Result.success(GET_PRODUCT_SUCCESS, "推荐成功", recommendations);
    }

    @PostMapping("/recordView")
    public Result recordProductView(@RequestBody ProductViewRecord record) {
        // 获取当前用户信息
        Map<String, Object> u = ThreadlocalUtils.get();
        System.out.println(u);
        String username = (String) u.get("username");

        // 将秒数转换为小时、分钟和秒
        int hours = record.getDuration() / 3600;
        int minutes = (record.getDuration() % 3600) / 60;
        int seconds = record.getDuration() % 60;
        String viewTimeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);

        productService.insertViewTime(username, viewTimeString, record.getProductName());

        return Result.success(RECORD_VIEW_SUCCESS, "记录成功");
    }

    @GetMapping("/getView")
    public Result getViewTime(@RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "12") int pageSize,
                              @RequestParam(required = false) String username){
        return Result.success(GET_RECORD_VIEW_SUCCESS, "获取成功",productService.getViewTimes(username, page, pageSize));
    }
}
