package org.example.coursedesign.service;

import org.example.coursedesign.pojo.Product;

import java.util.List;

public interface RecommendationService {
    public List<Product> recommendForUser(int userId, String username, int limit);
    public List<Product> recommendSimilarProducts(int productId, int limit);
}
