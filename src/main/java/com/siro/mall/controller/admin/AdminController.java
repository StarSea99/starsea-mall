package com.siro.mall.controller.admin;

import com.siro.mall.common.ServiceResultEnum;
import com.siro.mall.entity.AdminUser;
import com.siro.mall.service.AdminUserService;
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
 * @date 2021-01-22
 */
@Api(tags = "管理员controller")
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Resource
    private AdminUserService adminUserService;

    @ApiOperation(value = "后台登录页面跳转")
    @GetMapping("/login")
    public String login() {
        return "admin/login";
    }

    @ApiOperation(value = "后台首页跳转")
    @GetMapping({"","/","/index","/index.html"})
    public String index(HttpServletRequest request){
        request.setAttribute("path", "index");
        return "admin/index";
    }

    @ApiOperation(value = "后台登录")
    @PostMapping("/login")
    public String login(@RequestParam("userName") String userName,
                        @RequestParam("password") String password,
                        @RequestParam("verifyCode") String verifyCode,
                        HttpSession session) {
        if (StringUtils.isEmpty(verifyCode)) {
            session.setAttribute("errorMsg","验证码不能为空");
            return "admin/login";
        }
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)) {
            session.setAttribute("errorMsg","用户名或密码不能为空");
            return "admin/login";
        }
        String kaptchaCode = session.getAttribute("verifyCode") + "";
        if (StringUtils.isEmpty(kaptchaCode) || !verifyCode.equals(kaptchaCode)) {
            session.setAttribute("errorMsg","验证码错误");
            return "admin/login";
        }

        AdminUser adminUser = adminUserService.login(userName,password);
        if (adminUser != null){
            session.setAttribute("loginUserId",adminUser.getAdminUserId());
            session.setAttribute("loginUser",adminUser.getNickName());
            return "redirect:/admin/index";
        } else {
            session.setAttribute("errorMsg","登录失败");
            return "admin/login";
        }
    }

    @ApiOperation(value = "查询管理员个人信息")
    @GetMapping("/profile")
    public String profile(HttpServletRequest request) {
        Integer loginUserId = (int) request.getSession().getAttribute("loginUserId");
        AdminUser adminUser = adminUserService.getUserDetailById(loginUserId);
        if (adminUser == null) {
            return "admin/login";
        }
        request.setAttribute("path", "profile");
        request.setAttribute("loginUserName", adminUser.getLoginUserName());
        request.setAttribute("nickName", adminUser.getNickName());
        return "admin/profile";
    }

    @ApiOperation(value = "修改管理员密码")
    @PostMapping("/profile/password")
    @ResponseBody
    public String passwordUpdate(HttpServletRequest request, @RequestParam("originalPassword") String originalPassword,
                                 @RequestParam("newPassword") String newPassword) {
        if (StringUtils.isEmpty(originalPassword) || StringUtils.isEmpty(newPassword)) {
            return "参数不能为空";
        }
        Integer loginUserId = (int) request.getSession().getAttribute("loginUserId");
        if (adminUserService.updatePassword(loginUserId, originalPassword, newPassword)) {
            //修改成功后清空session中的数据，前端控制跳转至登录页
            request.getSession().removeAttribute("loginUserId");
            request.getSession().removeAttribute("loginUser");
            request.getSession().removeAttribute("errorMsg");
            return ServiceResultEnum.SUCCESS.getResult();
        } else {
            return "修改失败";
        }
    }

    @ApiOperation(value = "修改管理员名")
    @PostMapping("/profile/name")
    @ResponseBody
    public String nameUpdate(HttpServletRequest request, @RequestParam("loginUserName") String loginUserName,
                             @RequestParam("nickName") String nickName) {
        if (StringUtils.isEmpty(loginUserName) || StringUtils.isEmpty(nickName)) {
            return "参数不能为空";
        }
        Integer loginUserId = (int) request.getSession().getAttribute("loginUserId");
        if (adminUserService.updateName(loginUserId, loginUserName, nickName)) {
            return ServiceResultEnum.SUCCESS.getResult();
        } else {
            return "修改失败";
        }
    }

    @ApiOperation(value = "登出")
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        request.getSession().removeAttribute("loginUserId");
        request.getSession().removeAttribute("loginUser");
        request.getSession().removeAttribute("errorMsg");
        return "admin/login";
    }

}
