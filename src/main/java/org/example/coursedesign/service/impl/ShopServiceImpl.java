package org.example.coursedesign.service.impl;

import org.example.coursedesign.mapper.ShopMapper;
import org.example.coursedesign.pojo.PageInfo;
import org.example.coursedesign.pojo.Product;
import org.example.coursedesign.pojo.PurchaseHistory;
import org.example.coursedesign.pojo.SaleHistory;
import org.example.coursedesign.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.Transient;
import java.util.List;

@Service
public class ShopServiceImpl implements ShopService {
    @Autowired
    ShopMapper shopMapper;

    @Override
    public PageInfo<Product> getProductList(String product_name, String shop, int pageNum, int pageSize) {
        // 计算偏移量
        int offset = (pageNum - 1) * pageSize;

        // 查询数据列表
        List<Product> list = shopMapper.getProduct(shop, product_name, offset, pageSize);

        // 查询总数
        int total = shopMapper.getProductCount(shop, product_name);

        // 返回分页结果
        return new PageInfo(pageNum, pageSize, total, list);
    }

    @Override
    public PageInfo<SaleHistory> getSaleHistory(String shop, String product_name, int pageNum, int pageSize) {
        // 计算偏移量
        int offset = (pageNum - 1) * pageSize;

        // 查询数据列表
        List<SaleHistory> list = shopMapper.getSaleHistory(shop, product_name, offset, pageSize);

        // 查询总数
        int total = shopMapper.getSaleHistoryCount(shop, product_name);

        // 返回分页结果
        return new PageInfo(pageNum, pageSize, total, list);
    }

    @Override
    public void updateProduct(Product product) {
        shopMapper.updateProduct(product);
    }

    @Override
    public void deleteProduct(int id) {
        shopMapper.deleteProduct(id);
    }

    @Override
    public int insertProduct(Product product) {
        shopMapper.insertProduct(product);
        return shopMapper.getIdByProductName(product.getName());
    }

    @Override
    public String getProuuctImageById(int id) {
        return shopMapper.getImageUrlById(id);
    }

    @Override
    public void updateProductImageById(int id, String image) {
        shopMapper.updateImageUrl(id, image);
    }

    @Transactional
    @Override
    public void applyShop(String username, String shop) {
        shopMapper.setShop(username, shop);
        shopMapper.setPermission(username, 2);
    }

    @Override
    public void permitShop(String username) {
        shopMapper.setPermission(username, 1);
    }
}
