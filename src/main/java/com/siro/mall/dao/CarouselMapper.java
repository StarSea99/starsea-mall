package com.siro.mall.dao;

import com.siro.mall.entity.Carousel;
import com.siro.mall.utils.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author starsea
 * @date 2021-01-23
 */
public interface CarouselMapper {

    int deleteByPrimaryKey(Integer carouselId);

    int insert(Carousel record);

    int insertSelective(Carousel record);

    Carousel selectByPrimaryKey(Integer carouselId);

    int updateByPrimaryKeySelective(Carousel record);

    int updateByPrimaryKey(Carousel record);

    List<Carousel> findCarouselList(PageQueryUtil pageUtil);

    int getTotalCarousels(PageQueryUtil pageUtil);

    int deleteBatch(Integer[] ids);

    List<Carousel> findCarouselsByNum(@Param("number") int number);
}
