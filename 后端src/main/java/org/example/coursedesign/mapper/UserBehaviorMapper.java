package org.example.coursedesign.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.coursedesign.pojo.Cart;
import org.example.coursedesign.pojo.OrderDetail;
import org.example.coursedesign.pojo.ViewTime;

import java.util.List;

@Mapper
public interface UserBehaviorMapper {
    // 获取用户浏览历史
    @Select("SELECT * FROM view_time WHERE username = #{username} ORDER BY view_time DESC LIMIT 50")
    List<ViewTime> getUserViewHistory(String username);

    // 获取用户购买记录
    @Select("SELECT d.* FROM order_detail d JOIN order_table o ON d.order_id = o.order_id " +
            "WHERE d.user_id = #{userId} ORDER BY o.trade_time DESC LIMIT 50")
    List<OrderDetail> getUserPurchaseHistory(int userId);

    // 获取用户购物车商品
    @Select("SELECT * FROM cart WHERE user_id = #{userId} ORDER BY add_time DESC LIMIT 20")
    List<Cart> getUserCartItems(int userId);
}