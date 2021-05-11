package com.siro.mall.vo;

import java.io.Serializable;

/**
 * 商品详情页VO
 * @author starsea
 * @date 2021-01-24
 */
public class GoodsDetailVO implements Serializable {
    private Long goodsId;//商品id
    private String goodsName;//商品名
    private String goodsIntro;//商品介绍
    private String goodsCoverImg;//商品主图
    private String[] goodsCarouselList;//商品轮播图
    private Integer sellingPrice;//商品实际售价
    private Integer originalPrice;//商品价格
    private String goodsDetailContent;//商品详情

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGoodsIntro() {
        return goodsIntro;
    }

    public void setGoodsIntro(String goodsIntro) {
        this.goodsIntro = goodsIntro;
    }

    public String getGoodsCoverImg() {
        return goodsCoverImg;
    }

    public void setGoodsCoverImg(String goodsCoverImg) {
        this.goodsCoverImg = goodsCoverImg;
    }

    public Integer getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(Integer sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public Integer getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(Integer originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getGoodsDetailContent() {
        return goodsDetailContent;
    }

    public void setGoodsDetailContent(String goodsDetailContent) {
        this.goodsDetailContent = goodsDetailContent;
    }

    public String[] getGoodsCarouselList() {
        return goodsCarouselList;
    }

    public void setGoodsCarouselList(String[] goodsCarouselList) {
        this.goodsCarouselList = goodsCarouselList;
    }
}
