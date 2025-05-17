package org.example.coursedesign.mapper;

import org.apache.ibatis.annotations.*;
import org.example.coursedesign.pojo.Product;
import org.example.coursedesign.pojo.SaleHistory;

import java.util.List;

@Mapper
public interface ShopMapper {
    @Select("<script>" +
            "select *  " +
            "from userdb.products as p " +
            "<where>" +
            "   <if test=\"productName != null and productName != ''\"> AND p.name LIKE CONCAT('%', #{productName}, '%') </if>" +
            "   <if test='shop != null and shop != \"\"'> AND p.shop = #{shop} </if>" +
            "</where>" +
            "LIMIT #{pageSize} OFFSET #{offset}" +
            "</script>")
    List<Product> getProduct(@Param("shop") String shop,
                             @Param("productName") String productName,
                             @Param("offset") int offset,
                             @Param("pageSize") int pageSize);

    @Select("<script>" +
            "select count(id) " +
            "from userdb.products as p " +
            "<where>" +
            "   <if test=\"productName != null and productName != ''\"> AND p.name LIKE CONCAT('%', #{productName}, '%') </if>" +
            "   <if test='shop != null and shop != \"\"'> AND p.shop = #{shop} </if>" +
            "</where>" +
            "</script>")
    int getProductCount(@Param("shop") String shop,
                        @Param("productName") String productName);

    @Select("<script>" +
            "SELECT count(od.id) " +
            "FROM order_detail od " +
            "JOIN products p ON od.product_id = p.id " +
            "<where>" +
            "   <if test=\"productName != null and productName != ''\"> AND p.name LIKE CONCAT('%', #{productName}, '%') </if>" +
            "   <if test='shop != null and shop != \"\"'> AND p.shop = #{shop} </if>" +
            "</where>" +
            "</script>")
    int getSaleHistoryCount(@Param("shop") String shop,
                            @Param("productName") String productName);

    @Select("<script>" +
            "SELECT od.id, p.name as productName, od.quantity, o.trade_time, u.username, " +
            "(od.quantity * p.price) as totalPrice " +
            "FROM order_table o " +
            "JOIN order_detail od ON o.order_id = od.order_id " +
            "JOIN products p ON od.product_id = p.id " +
            "JOIN user u ON o.user_id = u.id " +
            "<where>" +
            "   <if test=\"productName != null and productName != ''\"> AND p.name LIKE CONCAT('%', #{productName}, '%') </if>" +
            "   <if test='shop != null and shop != \"\"'> AND p.shop = #{shop} </if>" +
            "</where>" +
            "LIMIT #{pageSize} OFFSET #{offset}" +
            "</script>")
    List<SaleHistory> getSaleHistory(@Param("shop") String shop,
                                     @Param("productName") String productName,
                                     @Param("offset") int offset,
                                     @Param("pageSize") int pageSize);

    @Delete("delete from products " +
            "where id = #{id}")
    void deleteProduct(@Param("id") int id);

    @Insert("insert into products (name, price, stock, shop, description, status, category) " +
            "values(#{name}, #{price}, #{stock}, #{shop}, #{description}, #{status}, #{category})")
    void insertProduct(Product product);


    @Update("<script>" +
            "UPDATE products " +
            "<set>" +
            "   <if test='name != null'>name = #{name},</if>" +
            "   <if test='description != null'>description = #{description},</if>" +
            "   <if test='price != 0'>price = #{price},</if>" +
            "   <if test='stock != 0'>stock = #{stock},</if>" +
            "</set>" +
            "WHERE id = #{id}" +
            "<if test='id == null or id == 0'>" +
            "   AND 1=0" +  // 确保id无效时不更新任何数据
            "</if>" +
            "</script>")
    void updateProduct(Product product);

    @Select("select image from products " +
            "where id = #{id}")
    String getImageUrlById(int id);

    @Update("update products " +
            "set image = #{image} " +
            "where id = #{id}")
    void updateImageUrl(int id, String image);

    @Select("select id from products " +
            "where name = #{productName}")
    int getIdByProductName(@Param("productName") String productName);

    @Update("update user " +
            "set permission = #{permission} " +
            "where username = #{username}")
    void setPermission(@Param("username") String username, @Param("permission") int permission);

    @Update("update user " +
            "set shop = #{shop} " +
            "where username = #{username}")
    void setShop(@Param("username") String username, @Param("shop") String shop);
}
