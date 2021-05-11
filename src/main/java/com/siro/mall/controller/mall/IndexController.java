package com.siro.mall.controller.mall;

import com.siro.mall.common.Constants;
import com.siro.mall.common.IndexConfigTypeEnum;
import com.siro.mall.service.CarouselService;
import com.siro.mall.service.CategoryService;
import com.siro.mall.service.IndexConfigService;
import com.siro.mall.vo.IndexCarouselVO;
import com.siro.mall.vo.IndexCategoryVO;
import com.siro.mall.vo.IndexConfigGoodsVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author starsea
 * @date 2021-01-22
 */
@Api(tags = "前台首页数据Controller")
@Controller
public class IndexController {

    @Resource
    private CarouselService carouselService;
    @Resource
    private IndexConfigService indexConfigService;
    @Resource
    private CategoryService categoryService;

    @ApiOperation(value = "跳转首页并显示数据")
    @GetMapping({"/index", "/", "/index.html"})
    public String indexPage(HttpServletRequest request) {
        //查询分类菜单
        List<IndexCategoryVO> categories = categoryService.getCategoriesForIndex();
        if (CollectionUtils.isEmpty(categories)) {
            return "error/error_5xx";
        }
        //查询轮播图
        List<IndexCarouselVO> carousels = carouselService.getCarouselsForIndex(Constants.INDEX_CAROUSEL_NUMBER);
        //查询热门商品
        List<IndexConfigGoodsVO> hotGoodses = indexConfigService.getConfigGoodsesForIndex(IndexConfigTypeEnum.INDEX_GOODS_HOT.getType(), Constants.INDEX_GOODS_HOT_NUMBER);
        //查询新品上线商品
        List<IndexConfigGoodsVO> newGoodses = indexConfigService.getConfigGoodsesForIndex(IndexConfigTypeEnum.INDEX_GOODS_NEW.getType(), Constants.INDEX_GOODS_NEW_NUMBER);
        //查询为你推荐
        List<IndexConfigGoodsVO> recommendGoodses = indexConfigService.getConfigGoodsesForIndex(IndexConfigTypeEnum.INDEX_GOODS_RECOMMOND.getType(), Constants.INDEX_GOODS_RECOMMOND_NUMBER);
        request.setAttribute("categories", categories);//分类数据
        request.setAttribute("carousels", carousels);//轮播图
        request.setAttribute("hotGoodses", hotGoodses);//热销商品
        request.setAttribute("newGoodses", newGoodses);//新品
        request.setAttribute("recommendGoodses", recommendGoodses);//推荐商品
        return "mall/index";
    }
}
