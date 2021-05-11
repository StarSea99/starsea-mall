package com.siro.mall.service.impl;

import com.siro.mall.common.Constants;
import com.siro.mall.common.ServiceResultEnum;
import com.siro.mall.dao.GoodsMapper;
import com.siro.mall.dao.ShoppingCartItemMapper;
import com.siro.mall.entity.Goods;
import com.siro.mall.entity.ShoppingCartItem;
import com.siro.mall.service.ShoppingCartService;
import com.siro.mall.utils.BeanUtil;
import com.siro.mall.vo.ShoppingCartItemVO;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author starsea
 * @date 2021-01-23
 */
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Resource
    private ShoppingCartItemMapper shoppingCartItemMapper;

    @Resource
    private GoodsMapper goodsMapper;

    @Override
    public String saveCartItem(ShoppingCartItem shoppingCartItem) {
        ShoppingCartItem temp = shoppingCartItemMapper.selectByUserIdAndGoodsId(shoppingCartItem.getUserId(), shoppingCartItem.getGoodsId());
        if (temp != null) {
            //已存在则修改该记录
            //todo count = tempCount + 1
            temp.setGoodsCount(shoppingCartItem.getGoodsCount());
            return updateCartItem(temp);
        }
        Goods newBeeMallGoods = goodsMapper.selectByPrimaryKey(shoppingCartItem.getGoodsId());
        //商品为空
        if (newBeeMallGoods == null) {
            return ServiceResultEnum.GOODS_NOT_EXIST.getResult();
        }
        int totalItem = shoppingCartItemMapper.selectCountByUserId(shoppingCartItem.getUserId()) + 1;
        //超出单个商品的最大数量
        if (shoppingCartItem.getGoodsCount() > Constants.SHOPPING_CART_ITEM_LIMIT_NUMBER) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR.getResult();
        }
        //超出最大数量
        if (totalItem > Constants.SHOPPING_CART_ITEM_TOTAL_NUMBER) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_TOTAL_NUMBER_ERROR.getResult();
        }
        //保存记录
        if (shoppingCartItemMapper.insertSelective(shoppingCartItem) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updateCartItem(ShoppingCartItem shoppingCartItem) {
        ShoppingCartItem newBeeMallShoppingCartItemUpdate = shoppingCartItemMapper.selectByPrimaryKey(shoppingCartItem.getCartItemId());
        if (newBeeMallShoppingCartItemUpdate == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        //超出单个商品的最大数量
        if (shoppingCartItem.getGoodsCount() > Constants.SHOPPING_CART_ITEM_LIMIT_NUMBER) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR.getResult();
        }
        //todo 数量相同不会进行修改
        //todo userId不同不能修改
        newBeeMallShoppingCartItemUpdate.setGoodsCount(shoppingCartItem.getGoodsCount());
        newBeeMallShoppingCartItemUpdate.setUpdateTime(new Date());
        //修改记录
        if (shoppingCartItemMapper.updateByPrimaryKeySelective(newBeeMallShoppingCartItemUpdate) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public ShoppingCartItem getCartItemById(Long shoppingCartItemId) {
        return shoppingCartItemMapper.selectByPrimaryKey(shoppingCartItemId);
    }

    @Override
    public Boolean deleteById(Long shoppingCartItemId) {
        //todo userId不同不能删除
        return shoppingCartItemMapper.deleteByPrimaryKey(shoppingCartItemId) > 0;
    }

    @Override
    public List<ShoppingCartItemVO> getMyShoppingCartItems(Long userId) {
        List<ShoppingCartItemVO> shoppingCartItemVOS = new ArrayList<>();
        List<ShoppingCartItem> newBeeMallShoppingCartItems = shoppingCartItemMapper.selectByUserId(userId, Constants.SHOPPING_CART_ITEM_TOTAL_NUMBER);
        if (!CollectionUtils.isEmpty(newBeeMallShoppingCartItems)) {
            //查询商品信息并做数据转换
            List<Long> newBeeMallGoodsIds = newBeeMallShoppingCartItems.stream().map(ShoppingCartItem::getGoodsId).collect(Collectors.toList());
            List<Goods> newBeeMallGoods = goodsMapper.selectByPrimaryKeys(newBeeMallGoodsIds);
            Map<Long, Goods> newBeeMallGoodsMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(newBeeMallGoods)) {
                newBeeMallGoodsMap = newBeeMallGoods.stream().collect(Collectors.toMap(Goods::getGoodsId, Function.identity(), (entity1, entity2) -> entity1));
            }
            for (ShoppingCartItem newBeeMallShoppingCartItem : newBeeMallShoppingCartItems) {
                ShoppingCartItemVO newBeeMallShoppingCartItemVO = new ShoppingCartItemVO();
                BeanUtil.copyProperties(newBeeMallShoppingCartItem, newBeeMallShoppingCartItemVO);
                if (newBeeMallGoodsMap.containsKey(newBeeMallShoppingCartItem.getGoodsId())) {
                    Goods newBeeMallGoodsTemp = newBeeMallGoodsMap.get(newBeeMallShoppingCartItem.getGoodsId());
                    newBeeMallShoppingCartItemVO.setGoodsCoverImg(newBeeMallGoodsTemp.getGoodsCoverImg());
                    String goodsName = newBeeMallGoodsTemp.getGoodsName();
                    // 字符串过长导致文字超出的问题
                    if (goodsName.length() > 28) {
                        goodsName = goodsName.substring(0, 28) + "...";
                    }
                    newBeeMallShoppingCartItemVO.setGoodsName(goodsName);
                    newBeeMallShoppingCartItemVO.setSellingPrice(newBeeMallGoodsTemp.getSellingPrice());
                    shoppingCartItemVOS.add(newBeeMallShoppingCartItemVO);
                }
            }
        }
        return shoppingCartItemVOS;
    }
}
