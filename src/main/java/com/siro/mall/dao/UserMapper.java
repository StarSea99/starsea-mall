package com.siro.mall.dao;

import com.siro.mall.entity.User;
import com.siro.mall.utils.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author starsea
 * @date 2021-01-23
 */
public interface UserMapper {
    int deleteByPrimaryKey(Long userId);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Long userId);

    User selectByLoginName(String loginName);

    User selectByLoginNameAndPasswd(@Param("loginName") String loginName, @Param("password") String password);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    List<User> findUserList(PageQueryUtil pageUtil);

    int getTotalUsers(PageQueryUtil pageUtil);

    int lockUserBatch(@Param("ids") Integer[] ids, @Param("lockStatus") int lockStatus);
}