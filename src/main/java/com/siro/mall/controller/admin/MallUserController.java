package com.siro.mall.controller.admin;

import com.siro.mall.service.UserService;
import com.siro.mall.utils.PageQueryUtil;
import com.siro.mall.utils.Result;
import com.siro.mall.utils.ResultGenerator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author starsea
 * @date 2021-01-23
 */
@Api(tags = "后台用户controller")
@Controller
@RequestMapping("/admin")
public class MallUserController {

    @Resource
    private UserService userService;

    @ApiOperation(value = "跳转用户页面")
    @GetMapping("/users")
    public String usersPage(HttpServletRequest request) {
        request.setAttribute("path", "users");
        return "admin/mall_user";
    }

    @ApiOperation(value = "列表")
    @RequestMapping(value = "/users/list", method = RequestMethod.GET)
    @ResponseBody
    public Result list(@RequestParam Map<String, Object> params) {
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(userService.getUsersPage(pageUtil));
    }

    @ApiOperation(value = "用户禁用与解除禁用(0-未锁定 1-已锁定)")
    @RequestMapping(value = "/users/lock/{lockStatus}", method = RequestMethod.POST)
    @ResponseBody
    public Result delete(@RequestBody Integer[] ids, @PathVariable int lockStatus) {
        if (ids.length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        if (lockStatus != 0 && lockStatus != 1) {
            return ResultGenerator.genFailResult("操作非法！");
        }
        if (userService.lockUsers(ids, lockStatus)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("禁用失败");
        }
    }

}
