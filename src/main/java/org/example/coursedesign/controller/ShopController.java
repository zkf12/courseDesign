package org.example.coursedesign.controller;

import org.example.coursedesign.pojo.Product;
import org.example.coursedesign.pojo.Result;
import org.example.coursedesign.pojo.User;
import org.example.coursedesign.service.ShopService;
import org.example.coursedesign.utils.ThreadlocalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

import static org.example.coursedesign.AppConstants.*;
import static org.example.coursedesign.utils.saveAvatarFileUtils.deleteImage;
import static org.example.coursedesign.utils.saveAvatarFileUtils.uploadImage;

@RestController
@RequestMapping("/shop")
public class ShopController {
    @Autowired
    ShopService shopService;

    @PostMapping("/getList")
    public Result getProductList(@RequestParam(required = false) String productName,
                                 @RequestParam(required = false) String shop,
                                 @RequestParam(defaultValue = "1") Integer pageNum,
                                 @RequestParam(defaultValue = "12") Integer pageSize) {
        return Result.success(GET_SHOP_SUCCESS,"获取成功",shopService.getProductList(productName,shop,pageNum,pageSize));
    }

    @PostMapping("/history")
    public Result getSaleHistory(@RequestParam(required = false) String productName,
                                     @RequestParam(required = false) String shop,
                                     @RequestParam(defaultValue = "1") Integer pageNum,
                                     @RequestParam(defaultValue = "12") Integer pageSize) {
        return Result.success(GET_SHOP_SUCCESS,"获取成功",shopService.getSaleHistory(shop,productName,pageNum,pageSize));
    }

    @PostMapping("/insert")
    public Result insertProduct(@RequestBody Product product) {
        int id = shopService.insertProduct(product);
        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        return Result.success(INSERT_PRODUCT_SUCCESS,"添加成功",data);
    }

    @PostMapping("/delete")
    public Result deleteProduct(@RequestParam(required = true) int id) {
        shopService.deleteProduct(id);
        return Result.success(DELETE_PRODUCT_SUCCESS,"删除成功");
    }

    @PostMapping("/updateImage")
    public Result updateProductImage(@RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
                                     @RequestParam(required = true) int productId) {
        try {
            String newImageName = null;
            // 如果有上传新头像
            if (avatarFile != null && !avatarFile.isEmpty()) {

                String imageUrl = IMAGE_DIRECTORY + shopService.getProuuctImageById(productId);
                System.out.println(imageUrl);

                // 使用工具类上传图片
                newImageName = uploadImage(
                        avatarFile,
                        IMAGE_DIRECTORY,
                        new String[]{"jpg", "jpeg", "png"},
                        2 * 1024 * 1024,
                        "productPicture/"
                );
                System.out.println(newImageName);

                // 删除旧头像文件
                deleteImage(imageUrl);
                System.out.println("deleted");
            }
            // 更新商品图片到数据库
            shopService.updateProductImageById(productId, newImageName);
            System.out.println("updated");

            return Result.success(UPDATE_PRODUCT_SUCCESS, "修改图片成功");
        } catch (IllegalArgumentException e) {
            // 文件验证失败
            return Result.error(UPDATE_ERROR, e.getMessage());
        } catch (Exception e) {
            // 其他错误
            return Result.error(UNKNOWN_ERROR, e.getMessage());
        }
    }

    @PostMapping("/update")
    public Result updateProduct(@RequestBody Product product) {
        shopService.updateProduct(product);
        return Result.success(UPDATE_PRODUCT_SUCCESS, "修改信息成功");
    }

    @GetMapping("/apply")
    public Result applyShop(@RequestParam(required = true) String shop) {
        Map<String, Object> u = ThreadlocalUtils.get();
        String username = (String) u.get("username");
        shopService.applyShop(username,shop);
        return Result.success(APPLY_SUCCESS,"申请成功");
    }

    @GetMapping("/permit")
    public Result permitShop(@RequestParam(required = true) String username) {
        Map<String, Object> u = ThreadlocalUtils.get();
        int permission = (Integer) u.get("permission");
        if (permission > 3) {
            shopService.permitShop(username);
            return Result.success(PERMIT_SUCCESS, "通过申请");
        } else {
            return Result.error(PERMIT_ERROR, "权限不足");
        }
    }
}
