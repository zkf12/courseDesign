package org.example.coursedesign.service;

import org.example.coursedesign.pojo.PageInfo;
import org.example.coursedesign.pojo.User;
import org.example.coursedesign.pojo.UserLogInfo;

import java.util.List;

public interface UserService {
    User findByUserName(String username);
    void register(String username, String password);
    void updateUser(User user, String username);
    void changePassword(String password, String username);
    PageInfo<User> getUserList(String username, int pageNum, int pageSize);
    void deleteUser(int id);
    User getUserById(int id);
    void setLoginLogout(String username, String ip, String login_or_logout);
    PageInfo<UserLogInfo> searchUserLog(String username, int pageNum, int pageSize);
    void deleteUserLog(int id);
}
