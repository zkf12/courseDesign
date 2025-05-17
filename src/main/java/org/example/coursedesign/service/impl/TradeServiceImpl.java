package org.example.coursedesign.service.impl;

import org.example.coursedesign.mapper.TradeMapper;
import org.example.coursedesign.pojo.*;
import org.example.coursedesign.service.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TradeServiceImpl implements TradeService {
    @Autowired
    TradeMapper tradeMapper;

    @Override
    public boolean addInCart(int user_id,int product_id) {
        if(tradeMapper.checkCart(user_id,product_id) == 0) {
            tradeMapper.addInCart(user_id,product_id);
            return true;
        }
        return false;
    }

    @Override
    public List<Product> getCart(int user_id) {
        return tradeMapper.getCart(user_id);
    }

    @Override
    public void deleteCart(int user_id, int product_id) {
        tradeMapper.deleteCart(user_id,product_id);
    }

    @Override
    public boolean checkProductStock(List<tradeDto> list) {
        for (tradeDto tradeDto : list) {
            int quantity = tradeMapper.getProductStock(tradeDto.getProductId());
            if(quantity < tradeDto.getQuantity()) {
                return false;
            }
        }
        return true;
    }

    @Override
    @Transactional
    public void setNewOrder(int user_id, double totalPrice, List<tradeDto> list) {
        tradeMapper.setNewOrder(user_id,totalPrice);
        int orderId = tradeMapper.getOrderId();
        for (tradeDto tradeDto : list) {
            tradeMapper.setNewOrderDetail(user_id,tradeDto.getProductId(),orderId,tradeDto.getQuantity());
            tradeMapper.changeProductStock(tradeDto.getProductId(),tradeDto.getQuantity());
        }
    }

    @Override
    public PageInfo<PurchaseHistory> getPurchaseHistory(String username, String product_name, String shop, int pageNum, int pageSize) {
        // 计算偏移量
        int offset = (pageNum - 1) * pageSize;

        // 查询数据列表
        List<PurchaseHistory> list = tradeMapper.getPurchaseHistory(username, product_name, shop, offset, pageSize);

        // 查询总数
        int total = tradeMapper.getPurchaseHistoryCount(username, product_name, shop);

        // 返回分页结果
        return new PageInfo(pageNum, pageSize, total, list);
    }

    @Override
    public List<HistoryDetail> getHistoryDetail(int order_id) {
        return tradeMapper.getHistoryDetail(order_id);
    }

    @Override
    public PageInfo<SalePrice> getSalePrice(int pageNum, int pageSize) {
        // 计算偏移量
        int offset = (pageNum - 1) * pageSize;

        // 查询数据列表
        List<SalePrice> list = tradeMapper.getMostSalePrices(pageSize, offset);

        // 查询总数
        int total = tradeMapper.getSalesCount();

        // 返回分页结果
        return new PageInfo(pageNum, pageSize, total, list);
    }

    @Override
    public PageInfo<SaleQuantity> getSaleQuantity(int pageNum, int pageSize) {
        // 计算偏移量
        int offset = (pageNum - 1) * pageSize;

        // 查询数据列表
        List<SaleQuantity> list = tradeMapper.getSaleQuantities(pageSize, offset);

        // 查询总数
        int total = tradeMapper.getSalesCount();

        // 返回分页结果
        return new PageInfo(pageNum, pageSize, total, list);
    }


}
