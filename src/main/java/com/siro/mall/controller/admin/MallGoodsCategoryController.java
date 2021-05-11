package com.siro.mall.controller.admin;

import com.siro.mall.common.MallCategoryLevelEnum;
import com.siro.mall.common.ServiceResultEnum;
import com.siro.mall.entity.GoodsCategory;
import com.siro.mall.service.CategoryService;
import com.siro.mall.utils.PageQueryUtil;
import com.siro.mall.utils.Result;
import com.siro.mall.utils.ResultGenerator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author starsea
 * @date 2021-01-23
 */
@Api(tags = "分类的Controller")
@Controller
@RequestMapping("/admin")
public class MallGoodsCategoryController {

    @Resource
    private CategoryService categoryService;

    @ApiOperation(value = "跳转到分类管理页面，并查询下级分类，返回上一级")
    @GetMapping("/categories")
    public String categoriesPage(HttpServletRequest request,
                                 @RequestParam("categoryLevel") Byte categoryLevel,
                                 @RequestParam("parentId") Long parentId,
                                 @RequestParam("backParentId") Long backParentId) {
        if (categoryLevel == null || categoryLevel < 1 || categoryLevel > 3) {
            return "error/error_5xx";
        }
        request.setAttribute("path", "mall_category");
        request.setAttribute("parentId", parentId);
        request.setAttribute("backParentId", backParentId);
        request.setAttribute("categoryLevel", categoryLevel);
        return "admin/mall_category";
    }

    @ApiOperation(value = "列表")
    @RequestMapping(value = "/categories/list", method = RequestMethod.GET)
    @ResponseBody
    public Result list(@RequestParam Map<String, Object> params) {
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit")) || StringUtils.isEmpty(params.get("categoryLevel")) || StringUtils.isEmpty(params.get("parentId"))) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(categoryService.getCategorisPage(pageUtil));
    }

    @ApiOperation(value = "分类列表")
    @RequestMapping(value = "/categories/listForSelect", method = RequestMethod.GET)
    @ResponseBody
    public Result listForSelect(@RequestParam("categoryId") Long categoryId) {
        if (categoryId == null || categoryId < 1) {
            return ResultGenerator.genFailResult("缺少参数！");
        }
        GoodsCategory category = categoryService.getGoodsCategoryById(categoryId);
        //既不是一级分类也不是二级分类则为不返回数据
        if (category == null || category.getCategoryLevel() == MallCategoryLevelEnum.LEVEL_THREE.getLevel()) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        Map categoryResult = new HashMap(2);
        if (category.getCategoryLevel() == MallCategoryLevelEnum.LEVEL_ONE.getLevel()) {
            //如果是一级分类则返回当前一级分类下的所有二级分类，以及二级分类列表中第一条数据下的所有三级分类列表
            //查询一级分类列表中第一个实体的所有二级分类
            List<GoodsCategory> secondLevelCategories = categoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(categoryId), MallCategoryLevelEnum.LEVEL_TWO.getLevel());
            if (!CollectionUtils.isEmpty(secondLevelCategories)) {
                //查询二级分类列表中第一个实体的所有三级分类
                List<GoodsCategory> thirdLevelCategories = categoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondLevelCategories.get(0).getCategoryId()), MallCategoryLevelEnum.LEVEL_THREE.getLevel());
                categoryResult.put("secondLevelCategories", secondLevelCategories);
                categoryResult.put("thirdLevelCategories", thirdLevelCategories);
            }
        }
        if (category.getCategoryLevel() == MallCategoryLevelEnum.LEVEL_TWO.getLevel()) {
            //如果是二级分类则返回当前分类下的所有三级分类列表
            List<GoodsCategory> thirdLevelCategories = categoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(categoryId), MallCategoryLevelEnum.LEVEL_THREE.getLevel());
            categoryResult.put("thirdLevelCategories", thirdLevelCategories);
        }
        return ResultGenerator.genSuccessResult(categoryResult);
    }

    @ApiOperation(value = "分类添加")
    @RequestMapping(value = "/categories/save", method = RequestMethod.POST)
    @ResponseBody
    public Result save(@RequestBody GoodsCategory goodsCategory) {
        if (Objects.isNull(goodsCategory.getCategoryLevel())
                || StringUtils.isEmpty(goodsCategory.getCategoryName())
                || Objects.isNull(goodsCategory.getParentId())
                || Objects.isNull(goodsCategory.getCategoryRank())) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = categoryService.saveCategory(goodsCategory);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    @ApiOperation(value = "分类修改")
    @RequestMapping(value = "/categories/update", method = RequestMethod.POST)
    @ResponseBody
    public Result update(@RequestBody GoodsCategory goodsCategory) {
        if (Objects.isNull(goodsCategory.getCategoryId())
                || Objects.isNull(goodsCategory.getCategoryLevel())
                || StringUtils.isEmpty(goodsCategory.getCategoryName())
                || Objects.isNull(goodsCategory.getParentId())
                || Objects.isNull(goodsCategory.getCategoryRank())) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = categoryService.updateGoodsCategory(goodsCategory);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    @ApiOperation(value = "分类详情")
    @GetMapping("/categories/info/{id}")
    @ResponseBody
    public Result info(@PathVariable("id") Long id) {
        GoodsCategory goodsCategory = categoryService.getGoodsCategoryById(id);
        if (goodsCategory == null) {
            return ResultGenerator.genFailResult("未查询到数据");
        }
        return ResultGenerator.genSuccessResult(goodsCategory);
    }

    @ApiOperation(value = "分类删除")
    @RequestMapping(value = "/categories/delete", method = RequestMethod.POST)
    @ResponseBody
    public Result delete(@RequestBody Integer[] ids) {
        if (ids.length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        if (categoryService.deleteBatch(ids)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("删除失败");
        }
    }
}
