package org.example.coursedesign.mapper;

import org.apache.ibatis.annotations.*;
import org.example.coursedesign.pojo.User;
import org.example.coursedesign.pojo.UserLogInfo;

import java.util.List;

@Mapper
public interface UserMapper {
    //登录登出数据收集
    @Insert("insert into login_logout_information(username, ip, login_or_logout) " +
            "values (#{username}, #{ip}, #{login_or_logout})")
    void insertImformation(String username, String ip, String login_or_logout);

    //查找
    @Select("select * from user where username=#{username}")
    User findByUserName(String username);

    //添加
    @Insert("insert into user(username, password,create_time)" +
            " values(#{username},#{password},now())")
    void addUser(String username, String password);

    //修改
    @Update("update user " +
            "set phone = #{user.phone}, " +
            "email = #{user.email}, " +
            "picture = #{user.picture}, " +
            "nickname = #{user.nickname} " +
            "where username = #{username}")
    void updateUser(User user, String username);

    @Update("update user " +
            "set password = #{password} " +
            "where username = #{username}")
    void changePassword(String password, String username);

    @Select("<script>" +
            "SELECT * from user " +
            "<where>" +
            "   <if test='username != null and username != \"\"'> AND user.username LIKE CONCAT('%', #{username}, '%') </if>" +
            "</where>" +
            "order by permission desc " +
            "LIMIT #{pageSize} OFFSET #{offset}" +
            "</script>")
    List<User> getUserList(@Param("username") String username,
                           @Param("pageSize") int pageSize,
                           @Param("offset") int offset);

    @Select("<script>" +
            "SELECT count(id) from user " +
            "<where>" +
            "   <if test='username != null and username != \"\"'> AND user.username LIKE CONCAT('%', #{username}, '%') </if>" +
            "</where>" +
            "</script>")
    int getUserCount(@Param("username") String username);

    @Delete("delete from user " +
            "where id = #{id};")
    void deleteUser(@Param("id") int id);

    @Select("select * from user " +
            "where id =#{id}")
    User getUserById(int id);

    @Select("<script>" +
            "SELECT * from login_logout_information " +
            "<where>" +
            "   <if test='username != null and username != \"\"'> AND username LIKE CONCAT('%', #{username}, '%') </if>" +
            "</where>" +
            "LIMIT #{pageSize} OFFSET #{offset}" +
            "</script>")
    List<UserLogInfo> getUserLogList(String username, int pageSize, int offset);

    @Select("<script>" +
            "SELECT count(*) from login_logout_information " +
            "<where>" +
            "   <if test='username != null and username != \"\"'> AND username LIKE CONCAT('%', #{username}, '%') </if>" +
            "</where>" +
            "</script>")
    int getUserLogCount(String username);

    @Delete("delete from login_logout_information " +
            "where id = #{id}")
    void deleteUserLog(int id);
}
