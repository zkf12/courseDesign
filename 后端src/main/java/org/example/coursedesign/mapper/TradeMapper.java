package org.example.coursedesign.mapper;

import org.apache.ibatis.annotations.*;
import org.example.coursedesign.pojo.*;

import java.util.List;

@Mapper
public interface TradeMapper {
    @Select("select count(*) from cart " +
            "where user_id = #{user_id} and product_id = #{product_id}")
    int checkCart(@Param("user_id") int user_id, @Param("product_id") int product_id);

    @Insert("insert into cart(user_id, product_id)" +
            " values(#{user_id},#{product_id})")
    void addInCart(@Param("user_id") int user_id, @Param("product_id") int product_id);

    @Select("select p.id, name, price, shop, image, description, status, category from cart join products p on p.id = cart.product_id " +
            "where user_id = #{user_id}")
    List<Product> getCart(@Param("user_id") int user_id);

    @Delete("delete from cart " +
            "where user_id = #{user_id} and product_id = #{product_id}")
    void deleteCart(@Param("user_id") int user_id, @Param("product_id") int product_id);

    @Select("select max(order_id) as order_id " +
            "from order_table;")
    int getOrderId();

    @Insert("insert into order_table(user_id,total_price) values (#{user_id},#{totalPrice})")
    void setNewOrder(@Param("user_id") int user_id, @Param("totalPrice") double totalPrice);

    @Insert("insert into order_detail (user_id, product_id, order_id, quantity) values (#{user_id}, #{product_id}, #{order_id}, #{quantity})")
    void setNewOrderDetail(@Param("user_id") int user_id, @Param("product_id") int product_id, @Param("order_id") int order_id, @Param("quantity") int quantity);

    @Select("select stock " +
            "from products " +
            "where id = #{product_id}")
    int getProductStock(@Param("product_id") int product_id);

    @Update("update products " +
            "set stock = stock - #{quantity} " +
            "where id = #{product_id}")
    void changeProductStock(@Param("product_id") int product_id, @Param("quantity") int quantity);

    @Select("<script>" +
            "SELECT distinct o.order_id, o.trade_time, o.total_price, u.username " +
            "FROM order_table o " +
            "JOIN order_detail od ON o.order_id = od.order_id " +
            "JOIN products p ON od.product_id = p.id " +
            "JOIN user u ON o.user_id = u.id " +
            "<where>" +
            "   <if test='username != null and username != \"\"'> AND u.username = #{username} </if>" +
            "   <if test=\"productName != null and productName != ''\"> AND p.name LIKE CONCAT('%', #{productName}, '%') </if>" +
            "   <if test='shop != null and shop != \"\"'> AND p.shop LIKE CONCAT('%', #{shop}, '%') </if>" +
            "</where>" +
            "LIMIT #{pageSize} OFFSET #{offset}" +
            "</script>")
    List<PurchaseHistory> getPurchaseHistory(@Param("username") String username,
                                             @Param("productName") String product_name,
                                             @Param("shop") String shop,
                                             @Param("offset") int offset,
                                             @Param("pageSize") int pageSize);
    @Select("<script>" +
            "SELECT COUNT(DISTINCT o.order_id) " +
            "FROM order_table o " +
            "JOIN order_detail od ON o.order_id = od.order_id " +
            "JOIN products p ON od.product_id = p.id " +
            "JOIN user u ON o.user_id = u.id " +
            "<where>" +
            "   <if test='username != null and username != \"\"'> AND u.username = #{username} </if>" +
            "   <if test=\"productName != null and productName != ''\"> AND p.name LIKE CONCAT('%', #{productName}, '%') </if>" +
            "   <if test='shop != null and shop != \"\"'> AND p.shop LIKE CONCAT('%', #{shop}, '%') </if>" +
            "</where>" +
            "</script>")
    int getPurchaseHistoryCount(@Param("username") String username,
                                @Param("productName") String product_name,
                                @Param("shop") String shop);

    @Select("select quantity,shop,price,products.name as productName " +
            "from order_detail " +
            "join products on order_detail.product_id = products.id " +
            "where order_id = #{orderId}")
    List<HistoryDetail> getHistoryDetail(@Param("orderId") int order_id);


    @Select("SELECT p.name AS productName, SUM(od.quantity) * p.price AS totalPrice " +
            "FROM order_detail od " +
            "JOIN products p ON od.product_id = p.id " +
            "GROUP BY od.product_id, p.name, p.price " +
            "ORDER BY totalPrice DESC " +
            "LIMIT #{pageSize} OFFSET #{offset}")
    List<SalePrice> getMostSalePrices(int pageSize, int offset);

    @Select("select count(distinct product_id) " +
            "from order_detail")
    int getSalesCount();

    @Select("SELECT p.name AS productName, SUM(od.quantity) AS totalQuantity " +
            "FROM order_detail od " +
            "JOIN products p ON od.product_id = p.id " +
            "GROUP BY od.product_id, p.name " +
            "ORDER BY totalQuantity DESC " +
            "LIMIT #{pageSize} OFFSET #{offset}")
    List<SaleQuantity> getSaleQuantities(int pageSize, int offset);

}
