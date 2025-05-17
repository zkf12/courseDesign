package org.example.coursedesign.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.coursedesign.pojo.ViewTime;
import org.example.coursedesign.pojo.categories;
import org.example.coursedesign.pojo.Product;

import java.util.List;

@Mapper
public interface ProductMapper {

    @Select("select * from product_categories")
    List<categories> getCategories();

    // 分页查询商品（动态条件）
    @Select({
            "<script>",
            "select * from products as p" +
            " join product_categories as c on c.category = p.category",
            "<where>",
            "   <if test='categoryId != null'> AND c.id = #{categoryId} </if>",
            "   <if test='keyword != null'> AND name LIKE CONCAT('%', #{keyword}, '%') </if>",
            "</where>",
            "limit #{offset}, #{pageSize}",
            "</script>"
    })
    List<Product> selectProducts(
            @Param("offset") int offset,
            @Param("pageSize") int pageSize,
            @Param("categoryId") Integer categoryId,
            @Param("keyword") String keyword
    );

    // 查询总数（用于分页）
    @Select({
            "<script>",
            "select count(*) from products as p" +
            " join product_categories as c on c.category = p.category",
            "<where>",
            "   <if test='categoryId != null'> AND c.id = #{categoryId} </if>",
            "   <if test='keyword != null'> AND name LIKE CONCAT('%', #{keyword}, '%') </if>",
            "</where>",
            "</script>"
    })
    int countProducts(
            @Param("categoryId") Integer categoryId,
            @Param("keyword") String keyword
    );


    @Insert("insert into view_time(username, time, product_name) " +
            "values (#{username}, #{viewTime}, #{product_name})")
    void insertViewTime(String username, String viewTime, String product_name);


    @Select({
            "<script>",
            "select * from view_time" +
            "<where>",
            "   <if test='username != null'> AND username LIKE CONCAT('%', #{username}, '%') </if>",
            "</where>",
            "limit #{offset}, #{pageSize}",
            "</script>"
    })
    List<ViewTime> getViewTime(@Param("username") String username,
                               @Param("offset") int offset,
                               @Param("pageSize") int pageSize);

    @Select({
            "<script>",
            "select count(*) from view_time" +
                    "<where>",
            "   <if test='username != null'> AND username LIKE CONCAT('%', #{username}, '%') </if>",
            "</where>",
            "</script>"
    })
    int getViewTimeCount(String username);


    // 根据ID查询商品
    @Select("SELECT * FROM products WHERE id = #{id}")
    Product findById(int id);

    // 查询同类商品
    @Select("SELECT * FROM products WHERE category = #{category} AND id != #{excludeId} ORDER BY RAND() LIMIT #{limit}")
    List<Product> findRelatedProducts(@Param("category") String category,
                                      @Param("excludeId") int excludeId,
                                      @Param("limit") int limit);

    // 查询热门商品
    @Select("SELECT p.* FROM products p JOIN " +
            "(SELECT id, COUNT(*) as view_count FROM view_time GROUP BY id ORDER BY view_count DESC LIMIT 100) v " +
            "ON p.id = v.id ORDER BY RAND() LIMIT #{limit}")
    List<Product> findPopularProducts(@Param("limit") int limit);
}
