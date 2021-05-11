package com.siro.mall.controller.common;

import com.siro.mall.common.Constants;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author starsea
 * @date 2021-01-22
 */
@Api(tags = "验证码处理")
@Controller
public class CommonController {

    @ApiOperation(value = "后台登录页面验证码处理")
    @GetMapping("/common/kaptcha")
    public void defaultKaptcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires",0);
        response.setContentType("image/png");

        //三个参数分别是宽、高、位数
        SpecCaptcha captcha = new SpecCaptcha(150,40,4);
        //设置类型 数字和字母组合
        captcha.setCharType(Captcha.TYPE_DEFAULT);
        //设置字体
        captcha.setCharType(Captcha.FONT_9);
        //验证码存入session
        request.getSession().setAttribute("verifyCode",captcha.text().toLowerCase());
        //输出图片流
        captcha.out(response.getOutputStream());
    }

    @ApiOperation("前台登录页面验证码处理")
    @GetMapping("/common/mall/kaptcha")
    public void mallKaptcha(HttpServletRequest request,HttpServletResponse response) throws IOException {
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires",0);
        response.setContentType("image/png");

        //三个参数分别是宽、高、位数
        SpecCaptcha captcha = new SpecCaptcha(110,40,4);
        //设置类型 数字和字母组合
        captcha.setCharType(Captcha.TYPE_DEFAULT);
        //设置字体
        captcha.setCharType(Captcha.FONT_9);
        //验证码存入session
        request.getSession().setAttribute(Constants.MALL_VERIFY_CODE_KEY,captcha.text().toLowerCase());
        //输出图片流
        captcha.out(response.getOutputStream());
    }

}
