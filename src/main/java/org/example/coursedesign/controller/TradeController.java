package org.example.coursedesign.controller;

import org.example.coursedesign.mapper.TradeMapper;
import org.example.coursedesign.pojo.Result;

import org.example.coursedesign.pojo.Product;
import org.example.coursedesign.pojo.tradeDto;
import org.example.coursedesign.service.TradeService;
import org.example.coursedesign.utils.ThreadlocalUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.example.coursedesign.AppConstants.*;

@RestController
@RequestMapping("/trade")
public class TradeController {
    @Autowired
    TradeService tradeService;

    @PostMapping("/add")
    public Result addInCart(@RequestParam int product_id){
        Map<String, Object> u = ThreadlocalUtils.get();
        int user_id = (int) u.get("id");
        if(tradeService.addInCart(user_id, product_id)){
            return Result.success(UPDATE_Cart_SUCCESS,"添加购物车成功");
        }
        return Result.error(UPDATE_Cart_SUCCESS,"物品已存在购物车");
    }

    @PostMapping("/getCart")
    public Result getCart(){
        Map<String, Object> u = ThreadlocalUtils.get();
        int user_id = (int) u.get("id");
        try{
            List<Product> data = tradeService.getCart(user_id);
            return Result.success(GET_CART_SUCCESS,"查询成功", data);
        }catch (Exception e){
            return Result.error(GET_CART_ERROR, "查询购物车失败");
        }
    }

    @PostMapping("/deleteCart")
    public Result deleteCart(@RequestParam int product_id){
        Map<String, Object> u = ThreadlocalUtils.get();
        int user_id = (int) u.get("id");
        try{
            tradeService.deleteCart(user_id, product_id);
            return Result.success(DELETE_CART_SUCCESS, "删除成功");
        }catch (Exception e){
            return Result.error(DELETE_CART_ERROR, "删除物品失败:" + e.getMessage());
        }
    }

    @PostMapping("/purchase")
    public Result purchase(@RequestBody List<tradeDto> productList){
        Map<String, Object> u = ThreadlocalUtils.get();
        int user_id = (int) u.get("id");
        //查询库存是否足够
        if(tradeService.checkProductStock(productList)) {
            double totalPrice = 0;
            for (tradeDto tradeDto : productList) {
                totalPrice += tradeDto.getQuantity() * tradeDto.getPrice();
            }
            tradeService.setNewOrder(user_id, totalPrice, productList);
            return  Result.success(PURCHASE_SUCCESS, "购买成功");
        } else {
            return Result.error(PURCHASE_ERROR, "没有足够库存");
        }
    }

    @PostMapping("/history")
    public Result getPurchaseHistory(@RequestParam(required = false) String username,
                                     @RequestParam(required = false) String productName,
                                     @RequestParam(required = false) String shop,
                                     @RequestParam(defaultValue = "1") Integer pageNum,
                                     @RequestParam(defaultValue = "12") Integer pageSize){
        return Result.success(GET_PURCHASE_HISTORY_SUCCESS,"查询历史记录成功",tradeService.getPurchaseHistory(username, productName, shop, pageNum, pageSize));
    }

    @PostMapping("/detail")
    public Result getHistoryDetail(@RequestParam int order_id){
        return Result.success(GET_PURCHASE_HISTORY_DETAIL_SUCCESS,"查询明细成功",tradeService.getHistoryDetail(order_id));
    }

    @GetMapping("/mostPrices")
    public Result getMostPrices(@RequestParam(defaultValue = "1") Integer pageNum,
                           @RequestParam(defaultValue = "5") Integer pageSize){
        return Result.success(GET_MOST_PRICE_SUCCESS, "查询销售额排行成功",tradeService.getSalePrice(pageNum, pageSize));
    }

    @GetMapping("/mostQuantity")
    public Result getmostQuantity(@RequestParam(defaultValue = "1") Integer pageNum,
                                @RequestParam(defaultValue = "5") Integer pageSize){
        return Result.success(GET_MOST_QUANTITY_SUCCESS, "查询销售量排行成功",tradeService.getSaleQuantity(pageNum, pageSize));
    }
}
