package com.siro.mall.service;

import com.siro.mall.entity.User;
import com.siro.mall.utils.PageQueryUtil;
import com.siro.mall.utils.PageResult;
import com.siro.mall.vo.UserVO;

import javax.servlet.http.HttpSession;

/**
 * @author starsea
 * @date 2021-01-23
 */
public interface UserService {
    //后台分页
    PageResult getUsersPage(PageQueryUtil pageUtil);

    //用户注册
    String register(String loginName, String password);

    //登录
    String login(String loginName, String passwordMD5, HttpSession httpSession);

    //用户信息修改并返回最新的用户信息
    UserVO updateUserInfo(User mallUser, HttpSession httpSession);

    //用户禁用与解除禁用(0-未锁定 1-已锁定)
    Boolean lockUsers(Integer[] ids, int lockStatus);
}
