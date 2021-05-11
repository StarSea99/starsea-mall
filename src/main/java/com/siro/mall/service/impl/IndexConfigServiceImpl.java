package com.siro.mall.service.impl;

import com.siro.mall.common.ServiceResultEnum;
import com.siro.mall.dao.GoodsMapper;
import com.siro.mall.dao.IndexConfigMapper;
import com.siro.mall.entity.Goods;
import com.siro.mall.entity.IndexConfig;
import com.siro.mall.service.IndexConfigService;
import com.siro.mall.utils.BeanUtil;
import com.siro.mall.utils.PageQueryUtil;
import com.siro.mall.utils.PageResult;
import com.siro.mall.vo.IndexConfigGoodsVO;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author starsea
 * @date 2021-01-23
 */
@Service
public class IndexConfigServiceImpl implements IndexConfigService {

    @Resource
    private IndexConfigMapper indexConfigMapper;

    @Resource
    private GoodsMapper goodsMapper;

    @Override
    public PageResult getConfigsPage(PageQueryUtil pageUtil) {
        List<IndexConfig> indexConfigs = indexConfigMapper.findIndexConfigList(pageUtil);
        int total = indexConfigMapper.getTotalIndexConfigs(pageUtil);
        PageResult pageResult = new PageResult(indexConfigs, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String saveIndexConfig(IndexConfig indexConfig) {
        //todo 判断是否存在该商品
        if (indexConfigMapper.insertSelective(indexConfig) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updateIndexConfig(IndexConfig indexConfig) {
        //todo 判断是否存在该商品
        IndexConfig temp = indexConfigMapper.selectByPrimaryKey(indexConfig.getConfigId());
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        if (indexConfigMapper.updateByPrimaryKeySelective(indexConfig) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public IndexConfig getIndexConfigById(Long id) {
        return null;
    }

    @Override
    public boolean deleteBatch(Long[] ids) {
        if (ids.length < 1) {
            return false;
        }
        //删除数据
        return indexConfigMapper.deleteBatch(ids) > 0;
    }

    @Override
    public List<IndexConfigGoodsVO> getConfigGoodsesForIndex(int configType, int number) {
        List<IndexConfigGoodsVO> indexConfigGoodsVOS = new ArrayList<>(number);
        List<IndexConfig> indexConfigs = indexConfigMapper.findIndexConfigsByTypeAndNum(configType, number);
        if (!CollectionUtils.isEmpty(indexConfigs)) {
            //取出所有的goodsId
            List<Long> goodsIds = indexConfigs.stream().map(IndexConfig::getGoodsId).collect(Collectors.toList());
            List<Goods> newBeeMallGoods = goodsMapper.selectByPrimaryKeys(goodsIds);
            indexConfigGoodsVOS = BeanUtil.copyList(newBeeMallGoods, IndexConfigGoodsVO.class);
            for (IndexConfigGoodsVO newBeeMallIndexConfigGoodsVO : indexConfigGoodsVOS) {
                String goodsName = newBeeMallIndexConfigGoodsVO.getGoodsName();
                String goodsIntro = newBeeMallIndexConfigGoodsVO.getGoodsIntro();
                // 字符串过长导致文字超出的问题
                if (goodsName.length() > 30) {
                    goodsName = goodsName.substring(0, 30) + "...";
                    newBeeMallIndexConfigGoodsVO.setGoodsName(goodsName);
                }
                if (goodsIntro.length() > 22) {
                    goodsIntro = goodsIntro.substring(0, 22) + "...";
                    newBeeMallIndexConfigGoodsVO.setGoodsIntro(goodsIntro);
                }
            }
        }
        return indexConfigGoodsVOS;
    }
}
