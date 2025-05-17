package org.example.coursedesign.service.impl;

import org.example.coursedesign.mapper.UserMapper;
import org.example.coursedesign.pojo.PageInfo;
import org.example.coursedesign.pojo.PurchaseHistory;
import org.example.coursedesign.pojo.User;
import org.example.coursedesign.pojo.UserLogInfo;
import org.example.coursedesign.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User findByUserName(String username) {
        return userMapper.findByUserName(username);
    }

    @Override
    public void register(String username, String password) {
        userMapper.addUser(username, password);
    }

    @Override
    public void updateUser(User user, String username) {
        userMapper.updateUser(user, username);
    }

    public void changePassword(String password, String username){
        userMapper.changePassword(password, username);
    }

    @Override
    public PageInfo<User> getUserList(String username, int pageNum, int pageSize) {
        // 计算偏移量
        int offset = (pageNum - 1) * pageSize;

        // 查询数据列表
        List<User> list = userMapper.getUserList(username, pageSize, offset);

        // 查询总数
        int total = userMapper.getUserCount(username);

        // 返回分页结果
        return new PageInfo(pageNum, pageSize, total, list);
    }

    @Override
    public void deleteUser(int id) {
        userMapper.deleteUser(id);
    }

    @Override
    public User getUserById(int id) {
        return userMapper.getUserById(id);
    }

    @Override
    public void setLoginLogout(String username, String ip, String login_or_logout) {
        userMapper.insertImformation(username, ip, login_or_logout);
    }

    @Override
    public PageInfo<UserLogInfo> searchUserLog(String username, int pageNum, int pageSize) {
        // 计算偏移量
        int offset = (pageNum - 1) * pageSize;

        // 查询数据列表
        List<UserLogInfo> list = userMapper.getUserLogList(username, pageSize, offset);

        // 查询总数
        int total = userMapper.getUserLogCount(username);

        // 返回分页结果
        return new PageInfo(pageNum, pageSize, total, list);
    }

    @Override
    public void deleteUserLog(int id) {
        userMapper.deleteUserLog(id);
    }
}
