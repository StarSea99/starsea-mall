package com.siro.mall.service;

import com.siro.mall.entity.GoodsCategory;
import com.siro.mall.utils.PageQueryUtil;
import com.siro.mall.utils.PageResult;
import com.siro.mall.vo.IndexCategoryVO;
import com.siro.mall.vo.SearchPageCategoryVO;

import java.util.List;

/**
 * @author starsea
 * @date 2021-01-23
 */
public interface CategoryService {
    PageResult getCategorisPage(PageQueryUtil pageUtil);

    String saveCategory(GoodsCategory goodsCategory);

    String updateGoodsCategory(GoodsCategory goodsCategory);

    GoodsCategory getGoodsCategoryById(Long id);

    Boolean deleteBatch(Integer[] ids);

    //返回分类数据(首页调用)
    List<IndexCategoryVO> getCategoriesForIndex();

    //返回分类数据(搜索页调用)
    SearchPageCategoryVO getCategoriesForSearch(Long categoryId);

    //根据parentId和level获取分类列表
    List<GoodsCategory> selectByLevelAndParentIdsAndNumber(List<Long> parentIds, int categoryLevel);
}
