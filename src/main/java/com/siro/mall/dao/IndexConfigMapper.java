package com.siro.mall.dao;

import com.siro.mall.entity.IndexConfig;
import com.siro.mall.utils.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author starsea
 * @date 2021-01-23
 */
public interface IndexConfigMapper {
    int deleteByPrimaryKey(Long configId);

    int insert(IndexConfig record);

    int insertSelective(IndexConfig record);

    IndexConfig selectByPrimaryKey(Long configId);

    int updateByPrimaryKeySelective(IndexConfig record);

    int updateByPrimaryKey(IndexConfig record);

    List<IndexConfig> findIndexConfigList(PageQueryUtil pageUtil);

    int getTotalIndexConfigs(PageQueryUtil pageUtil);

    int deleteBatch(Long[] ids);

    List<IndexConfig> findIndexConfigsByTypeAndNum(@Param("configType") int configType, @Param("number") int number);
}