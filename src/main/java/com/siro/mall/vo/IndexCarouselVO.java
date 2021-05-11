package com.siro.mall.vo;

import java.io.Serializable;

/**
 * 首页轮播图VO
 * @author starsea
 * @date 2021-01-24
 */
public class IndexCarouselVO implements Serializable {

    private String carouselUrl;
    private String redirectUrl;

    public String getCarouselUrl() {
        return carouselUrl;
    }

    public void setCarouselUrl(String carouselUrl) {
        this.carouselUrl = carouselUrl;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }
}
