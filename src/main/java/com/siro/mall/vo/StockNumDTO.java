package com.siro.mall.vo;

import io.swagger.annotations.ApiModel;

/**
 * @author starsea
 * @date 2021-01-23
 */
@ApiModel(value = "库存修改所需实体类")
public class StockNumDTO {
    private Long goodsId;
    private Integer goodsCount;

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getGoodsCount() {
        return goodsCount;
    }

    public void setGoodsCount(Integer goodsCount) {
        this.goodsCount = goodsCount;
    }
}
