package org.example.coursedesign.service;

import org.example.coursedesign.pojo.*;

import java.util.List;

public interface TradeService {
    boolean addInCart(int user_id,int product_id);
    List<Product> getCart(int user_id);
    void deleteCart(int user_id,int product_id);
    boolean checkProductStock(List<tradeDto> list);
    void setNewOrder(int user_id,double totalPrice,List<tradeDto> list);
    PageInfo<PurchaseHistory> getPurchaseHistory(String username, String product_name, String shop, int pageNum, int pageSize);
    List<HistoryDetail> getHistoryDetail(int order_id);
    PageInfo<SalePrice> getSalePrice(int pageNum, int pageSize);
    PageInfo<SaleQuantity> getSaleQuantity(int pageNum, int pageSize);
}
