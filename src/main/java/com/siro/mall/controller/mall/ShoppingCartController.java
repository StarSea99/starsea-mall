package com.siro.mall.controller.mall;

import com.siro.mall.common.Constants;
import com.siro.mall.common.ServiceResultEnum;
import com.siro.mall.entity.ShoppingCartItem;
import com.siro.mall.service.ShoppingCartService;
import com.siro.mall.utils.Result;
import com.siro.mall.utils.ResultGenerator;
import com.siro.mall.vo.ShoppingCartItemVO;
import com.siro.mall.vo.UserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author starsea
 * @date 2021-01-23
 */
@Api(tags = "前台购物车controller")
@Controller
public class ShoppingCartController {

    @Resource
    private ShoppingCartService shoppingCartService;

    @ApiOperation(value = "查询购物车中商品")
    @GetMapping("/shop-cart")
    public String cartListPage(HttpServletRequest request, HttpSession httpSession) {
        UserVO user = (UserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        int itemsTotal = 0;
        int priceTotal = 0;
        List<ShoppingCartItemVO> myShoppingCartItems = shoppingCartService.getMyShoppingCartItems(user.getUserId());
        if (!CollectionUtils.isEmpty(myShoppingCartItems)) {
            //购物项总数
            itemsTotal = myShoppingCartItems.stream().mapToInt(ShoppingCartItemVO::getGoodsCount).sum();
            if (itemsTotal < 1) {
                return "error/error_5xx";
            }
            //总价
            for (ShoppingCartItemVO shoppingCartItemVO : myShoppingCartItems) {
                priceTotal += shoppingCartItemVO.getGoodsCount() * shoppingCartItemVO.getSellingPrice();
            }
            if (priceTotal < 1) {
                return "error/error_5xx";
            }
        }
        request.setAttribute("itemsTotal", itemsTotal);
        request.setAttribute("priceTotal", priceTotal);
        request.setAttribute("myShoppingCartItems", myShoppingCartItems);
        return "mall/cart";
    }

    @ApiOperation(value = "详情页添加至购物车")
    @PostMapping("/shop-cart")
    @ResponseBody
    public Result saveShoppingCartItem(@RequestBody ShoppingCartItem shoppingCartItem,
                                                 HttpSession httpSession) {
        UserVO user = (UserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        shoppingCartItem.setUserId(user.getUserId());
        //todo 判断数量
        String saveResult = shoppingCartService.saveCartItem(shoppingCartItem);
        //添加成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(saveResult)) {
            return ResultGenerator.genSuccessResult();
        }
        //添加失败
        return ResultGenerator.genFailResult(saveResult);
    }

    @ApiOperation(value = "修改购物车数量")
    @PutMapping("/shop-cart")
    @ResponseBody
    public Result updateShoppingCartItem(@RequestBody ShoppingCartItem shoppingCartItem, HttpSession httpSession) {
        UserVO user = (UserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        shoppingCartItem.setUserId(user.getUserId());
        //todo 判断数量
        String updateResult = shoppingCartService.updateCartItem(shoppingCartItem);
        //修改成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(updateResult)) {
            return ResultGenerator.genSuccessResult();
        }
        //修改失败
        return ResultGenerator.genFailResult(updateResult);
    }

    @ApiOperation(value = "删除购物车指定商品")
    @DeleteMapping("/shop-cart/{shoppingCartItemId}")
    @ResponseBody
    public Result updateShoppingCartItem(@PathVariable("shoppingCartItemId") Long shoppingCartItemId,
                                                   HttpSession httpSession) {
        //UserVO user = (UserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        Boolean deleteResult = shoppingCartService.deleteById(shoppingCartItemId);
        //删除成功
        if (deleteResult) {
            return ResultGenerator.genSuccessResult();
        }
        //删除失败
        return ResultGenerator.genFailResult(ServiceResultEnum.OPERATE_ERROR.getResult());
    }

    @ApiOperation(value = "购物车点击去结算")
    @GetMapping("/shop-cart/settle")
    public String settlePage(HttpServletRequest request, HttpSession httpSession) {
        int priceTotal = 0;
        UserVO user = (UserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        List<ShoppingCartItemVO> myShoppingCartItems = shoppingCartService.getMyShoppingCartItems(user.getUserId());
        if (CollectionUtils.isEmpty(myShoppingCartItems)) {
            //无数据则不跳转至结算页
            return "/shop-cart";
        } else {
            //总价
            for (ShoppingCartItemVO shoppingCartItemVO : myShoppingCartItems) {
                priceTotal += shoppingCartItemVO.getGoodsCount() * shoppingCartItemVO.getSellingPrice();
            }
            if (priceTotal < 1) {
                return "error/error_5xx";
            }
        }
        request.setAttribute("priceTotal", priceTotal);
        request.setAttribute("myShoppingCartItems", myShoppingCartItems);
        return "mall/order-settle";
    }
}

