package org.example.coursedesign.service;

import org.example.coursedesign.pojo.PageInfo;
import org.example.coursedesign.pojo.Product;
import org.example.coursedesign.pojo.SaleHistory;
import org.springframework.stereotype.Service;

@Service
public interface ShopService {
    PageInfo<Product> getProductList(String product_name, String shop, int pageNum, int pageSize);
    PageInfo<SaleHistory> getSaleHistory(String shop, String product_name, int pageNum, int pageSize);
    void updateProduct(Product product);
    void deleteProduct(int id);
    int insertProduct(Product product);
    String getProuuctImageById(int id);
    void updateProductImageById(int id, String image);
    void applyShop(String username, String shop);
    void permitShop(String username);
}
