package com.siro.mall.service;

import com.siro.mall.entity.ShoppingCartItem;
import com.siro.mall.vo.ShoppingCartItemVO;

import java.util.List;

/**
 * @author starsea
 * @date 2021-01-23
 */
public interface ShoppingCartService {
    //保存商品至购物车中
    String saveCartItem(ShoppingCartItem shoppingCartItem);

    //修改购物车中的属性
    String updateCartItem(ShoppingCartItem shoppingCartItem);

    //获取购物项详情
    ShoppingCartItem getCartItemById(Long shoppingCartItemId);

    //删除购物车中的商品
    Boolean deleteById(Long shoppingCartItemId);

    //获取我的购物车中的列表数据
    List<ShoppingCartItemVO> getMyShoppingCartItems(Long userId);
}
