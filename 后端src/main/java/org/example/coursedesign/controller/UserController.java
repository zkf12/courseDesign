package org.example.coursedesign.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.coursedesign.pojo.LoginDto;
import org.example.coursedesign.pojo.Result;
import org.example.coursedesign.pojo.User;
import org.example.coursedesign.service.UserService;
import org.example.coursedesign.utils.JwtUtils;
import org.example.coursedesign.utils.ThreadlocalUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import static org.example.coursedesign.AppConstants.*;
import static org.example.coursedesign.utils.saveAvatarFileUtils.deleteImage;
import static org.example.coursedesign.utils.saveAvatarFileUtils.uploadImage;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;


    @PostMapping("/register")
    public Result register(@RequestBody LoginDto user) {
        //查询用户是否存在
        User u = userService.findByUserName(user.getUsername());
        if (u != null) {
            //非法
            return Result.error(REGISTER_ERROR, "用户名已被占用");
        }else{
            if (user.getPassword().equals(user.getConfirmPassword())) {
                //注册
                userService.register(user.getUsername(), user.getPassword());
                return Result.success(REGISTER_SUCCESS, "注册成功");
            }
            else {
                return Result.error(REGISTER_ERROR, "两次输入密码不一致");
            }
        }
    }
    @PostMapping("/login")
    public Result login(@RequestBody LoginDto user, HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();

        Enumeration<String> headers = request.getHeaderNames();
        while (headers.hasMoreElements()) {
            String header = headers.nextElement();
            System.out.println(header + ": " + request.getHeader(header));
        }

        User u = userService.findByUserName(user.getUsername());
        if (u != null && u.getPassword().equals(user.getPassword())) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", u.getId());
            map.put("username", u.getUsername());
            map.put("permission", u.getPermission());
            Map<String, Object> data = new HashMap<>();
            data.put("jwt", JwtUtils.createToken(map));
            data.put("username", u.getUsername());
            data.put("permission", u.getPermission());
            data.put("nickname", u.getNickname());
            data.put("picture", u.getPicture());
            data.put("shop", u.getShop());
            userService.setLoginLogout(u.getUsername(),ipAddress,"login");
            return Result.success(LOGIN_SUCCESS, "登陆成功", data);
        }else {
            if(u == null){
                return Result.error(LOGIN_ERROR, "用户名错误");
            }
            return Result.error(LOGIN_ERROR, "密码错误");
        }
    }
    @PostMapping("/logout")
    public Result logout(HttpServletRequest request) {
        Map<String, Object> u = ThreadlocalUtils.get();
        User user = userService.findByUserName((String) u.get("username"));
        String ipAddress = request.getRemoteAddr();
        userService.setLoginLogout(user.getUsername(),ipAddress,"logout");
        return Result.success(LOGOUT_SUCCESS,"注销成功");
    }

    @PostMapping("/changeUserInfo")
    public Result changeUserInfo(@RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
                                       @RequestParam String nickname,
                                       @RequestParam String email,
                                       @RequestParam String phone) {
        Map<String, Object> u = ThreadlocalUtils.get();
        User user = userService.findByUserName((String) u.get("username"));

        try {
            String newImageName = null;
            user.setNickname(nickname);
            user.setEmail(email);
            user.setPhone(phone);

            // 如果有上传新头像
            if (avatarFile != null && !avatarFile.isEmpty()) {
                String imageUrl = IMAGE_DIRECTORY + user.getPicture();
                System.out.println(imageUrl);

                // 使用工具类上传图片
                newImageName = uploadImage(
                        avatarFile,
                        IMAGE_DIRECTORY,
                        new String[]{"jpg", "jpeg", "png"},
                        2 * 1024 * 1024,
                        "userPicture/"
                );
                System.out.println(newImageName);

                // 删除旧头像文件
                deleteImage(imageUrl);
                System.out.println("deleted");
                user.setPicture(newImageName);
            }
            System.out.println(user);
            // 更新用户信息到数据库
            userService.updateUser(user, (String) u.get("username"));
            System.out.println("updated");

            // 构造响应
            Map<String, Object> data = new HashMap<>();
            if (newImageName != null) {
                data.put("avatarUrl", newImageName);
                data.put("nickname", user.getNickname());
            }
            return Result.success(UPDATE_SUCCESS, "修改成功", data );

        } catch (IllegalArgumentException e) {
            // 文件验证失败
            return Result.error(UPDATE_ERROR, e.getMessage());
        } catch (Exception e) {
            // 其他错误
            return Result.error(UNKNOWN_ERROR, e.getMessage());
        }
    }

    @PostMapping("/changePassword")
    public Result changePassword(@RequestParam String oldPassword, @RequestParam String password, @RequestParam String confirmPassword) {
        Map<String, Object> u = ThreadlocalUtils.get();
        User user = userService.findByUserName((String) u.get("username"));
        if(!oldPassword.equals(user.getPassword())){
            return Result.error(CHANGE_PASSWORD_ERROR, "旧密码错误");
        }
        if(user.getPassword().equals(password)){
            return Result.error(CHANGE_PASSWORD_ERROR, "新旧密码相同");
        }
        if(password.equals(confirmPassword)) {
            userService.changePassword(password, user.getUsername());
            return Result.success(CHANGE_PASSWORD_SUCCESS, "密码修改成功", null);
        }else{
            return Result.error(CHANGE_PASSWORD_ERROR, "两次输入密码不同");
        }
    }

    @GetMapping("/getUser")
    public Result getUserList(@RequestParam(required = false) String username,
                              @RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "12") Integer pageSize){
        Map<String, Object> u = ThreadlocalUtils.get();
        User user = userService.findByUserName((String) u.get("username"));
        if(user.getPermission() > 3){
            return Result.success(GET_USER_SUCCESS,"查询成功",userService.getUserList(username,pageNum,pageSize));
        } else {
            return Result.error(GET_USER_ERROR, "权限不足");
        }
    }

    @GetMapping("/delete")
    public Result deleteUser(@RequestParam int id) {
        User user = userService.getUserById(id);
        String imageUrl = IMAGE_DIRECTORY + user.getPicture();
        deleteImage(imageUrl);
        userService.deleteUser(id);
        return Result.success(DELETE_USER_SUCCESS, "删除成功");
    }

    @GetMapping("/infoSearch")
    public Result getUserLog(@RequestParam(required = false) String username,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "12") Integer pageSize) {
        Map<String, Object> u = ThreadlocalUtils.get();
        User user = userService.findByUserName((String) u.get("username"));
        if(user.getPermission() > 3){
            return Result.success(GET_LOG_SUCCESS, "查询成功", userService.searchUserLog(username, pageNum, pageSize));
        } else {
            return Result.error(GET_USER_ERROR, "权限不足");
        }
    }

    @GetMapping("/logDelete")
    public Result logDelete(@RequestParam int id) {
        Map<String, Object> u = ThreadlocalUtils.get();
        User user = userService.findByUserName((String) u.get("username"));
        if(user.getPermission() > 3){
            userService.deleteUserLog(id);
            return Result.success(DELETE_LOG_SUCCESS, "删除成功");
        } else {
            return Result.error(DELETE_LOG_ERROR,"权限不足");
        }
    }
}
