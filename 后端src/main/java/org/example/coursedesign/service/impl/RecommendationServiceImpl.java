package org.example.coursedesign.service.impl;

import org.example.coursedesign.mapper.ProductMapper;
import org.example.coursedesign.mapper.UserBehaviorMapper;
import org.example.coursedesign.pojo.Cart;
import org.example.coursedesign.pojo.OrderDetail;
import org.example.coursedesign.pojo.Product;
import org.example.coursedesign.pojo.ViewTime;
import org.example.coursedesign.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RecommendationServiceImpl implements RecommendationService {
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private UserBehaviorMapper userBehaviorMapper;

    // 基于用户行为的混合推荐
    public List<Product> recommendForUser(int userId, String username, int limit) {
        // 1. 获取用户数据
        List<ViewTime> viewHistories = userBehaviorMapper.getUserViewHistory(username);
        List<OrderDetail> purchases = userBehaviorMapper.getUserPurchaseHistory(userId);
        List<Cart> cartItems = userBehaviorMapper.getUserCartItems(userId);

        // 2. 收集用户感兴趣的类别
        Set<String> interestedCategories = new HashSet<>();

        // 从浏览历史中获取类别
        for (ViewTime view : viewHistories) {
            Product product = productMapper.findById( view.getId());
            if (product != null) {
                interestedCategories.add(product.getCategory());
            }
        }

        // 从购买记录中获取类别
        for (OrderDetail purchase : purchases) {
            Product product = productMapper.findById(purchase.getProductId());
            if (product != null) {
                interestedCategories.add(product.getCategory());
            }
        }

        // 3. 生成推荐
        List<Product> recommendations = new ArrayList<>();

        // 策略1: 推荐同类商品
        for (String category : interestedCategories) {
            List<Product> relatedProducts = productMapper.findRelatedProducts(category, 0, limit);
            recommendations.addAll(relatedProducts);
        }

        // 策略2: 如果推荐不足，补充热门商品
        if (recommendations.size() < limit) {
            int remaining = limit - recommendations.size();
            List<Product> popularProducts = productMapper.findPopularProducts(remaining);
            recommendations.addAll(popularProducts);
        }

        // 4. 去重和随机排序
        recommendations = recommendations.stream()
                .distinct()
                .sorted((a, b) -> Math.random() > 0.5 ? 1 : -1)
                .limit(limit)
                .collect(Collectors.toList());

        return recommendations;
    }

    // 基于商品的相似推荐
    public List<Product> recommendSimilarProducts(int productId, int limit) {
        Product product = productMapper.findById(productId);
        if (product == null) {
            return productMapper.findPopularProducts(limit);
        }

        return productMapper.findRelatedProducts(product.getCategory(), productId, limit);
    }
}
