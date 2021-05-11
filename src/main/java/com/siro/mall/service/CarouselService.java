package com.siro.mall.service;

import com.siro.mall.entity.Carousel;
import com.siro.mall.utils.PageQueryUtil;
import com.siro.mall.utils.PageResult;
import com.siro.mall.vo.IndexCarouselVO;

import java.util.List;

/**
 * @author starsea
 * @date 2021-01-23
 */
public interface CarouselService {
    //查询列表页
    PageResult getCarouselPage(PageQueryUtil pageUtil);

    //轮播图添加
    String saveCarousel(Carousel carousel);

    //轮播图修改
    String updateCarousel(Carousel carousel);

    //根据id查询轮播图信息
    Carousel getCarouselById(Integer id);

    //根据id删除多个轮播图
    boolean deleteBatch(Integer[] ids);

    //返回固定数量的轮播图对象(首页调用)
    public List<IndexCarouselVO> getCarouselsForIndex(int number);
}
