package com.siro.mall.controller.mall;

import com.siro.mall.common.Constants;
import com.siro.mall.common.ServiceResultEnum;
import com.siro.mall.entity.User;
import com.siro.mall.service.UserService;
import com.siro.mall.utils.MD5Util;
import com.siro.mall.utils.Result;
import com.siro.mall.utils.ResultGenerator;
import com.siro.mall.vo.UserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author starsea
 * @date 2021-01-23
 */
@Api(tags = "前台用户controller")
@Controller
public class UserController {

    @Resource
    private UserService userService;

    @ApiOperation("跳转到注册页面")
    @GetMapping({"/register", "register.html"})
    public String registerPage() {
        return "mall/register";
    }

    @ApiOperation("用户注册")
    @PostMapping("/register")
    @ResponseBody
    public Result register(@RequestParam("loginName") String loginName,
                           @RequestParam("password") String password,
                           @RequestParam("verifyCode") String verifyCode,
                           HttpSession httpSession) {
        if (StringUtils.isEmpty(loginName)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_NAME_NULL.getResult());
        }
        if (StringUtils.isEmpty(password)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_PASSWORD_NULL.getResult());
        }
        if (StringUtils.isEmpty(verifyCode)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_VERIFY_CODE_NULL.getResult());
        }
        String kaptchaCode = httpSession.getAttribute(Constants.MALL_VERIFY_CODE_KEY) + "";
        if (StringUtils.isEmpty(kaptchaCode) || !verifyCode.toLowerCase().equals(kaptchaCode)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_VERIFY_CODE_ERROR.getResult());
        }
        //todo 清verifyCode
        String registerResult = userService.register(loginName, password);
        //注册成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(registerResult)) {
            return ResultGenerator.genSuccessResult();
        }
        //注册失败
        return ResultGenerator.genFailResult(registerResult);
    }

    @ApiOperation("跳转到登录页面")
    @GetMapping({"/login", "login.html"})
    public String loginPage() {
        return "mall/login";
    }

    @ApiOperation("用户登录")
    @PostMapping("/login")
    @ResponseBody
    public Result login(@RequestParam("loginName") String loginName,
                        @RequestParam("password") String password,
                        @RequestParam("verifyCode") String verifyCode,
                        HttpSession httpSession) {
        if (StringUtils.isEmpty(loginName)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_NAME_NULL.getResult());
        }
        if (StringUtils.isEmpty(password)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_PASSWORD_NULL.getResult());
        }
        if (StringUtils.isEmpty(verifyCode)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_VERIFY_CODE_NULL.getResult());
        }
        String kaptchaCode = httpSession.getAttribute(Constants.MALL_VERIFY_CODE_KEY) + "";
        if (StringUtils.isEmpty(kaptchaCode) || !verifyCode.toLowerCase().equals(kaptchaCode)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_VERIFY_CODE_ERROR.getResult());
        }
        //todo 清verifyCode
        String loginResult = userService.login(loginName, MD5Util.MD5Encode(password, "UTF-8"), httpSession);
        System.out.println("loginResult = " + loginResult);
        //登录成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(loginResult)) {
            return ResultGenerator.genSuccessResult();
        }
        //登录失败
        return ResultGenerator.genFailResult(loginResult);
    }

    @ApiOperation("登出")
    @GetMapping("/logout")
    public String logout(HttpSession httpSession) {
        httpSession.removeAttribute(Constants.MALL_USER_SESSION_KEY);
        return "mall/login";
    }

    @ApiOperation(value = "跳转个人中心页面")
    @GetMapping("/personal")
    public String personalPage(HttpServletRequest request) {
        //request.setAttribute("path", "personal");
        return "mall/personal";
    }

    @ApiOperation(value = "修改收货地址")
    @PostMapping("/personal/updateInfo")
    @ResponseBody
    public Result updateInfo(@RequestBody User mallUser, HttpSession httpSession) {
        UserVO mallUserTemp = userService.updateUserInfo(mallUser,httpSession);
        if (mallUserTemp == null) {
            Result result = ResultGenerator.genFailResult("修改失败");
            return result;
        } else {
            //返回成功
            Result result = ResultGenerator.genSuccessResult();
            return result;
        }
    }
}
