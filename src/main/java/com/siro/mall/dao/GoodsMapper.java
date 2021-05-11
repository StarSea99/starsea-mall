package com.siro.mall.dao;

import com.siro.mall.entity.Goods;
import com.siro.mall.utils.PageQueryUtil;
import com.siro.mall.vo.StockNumDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author starsea
 * @date 2021-01-23
 */
public interface GoodsMapper {
    int deleteByPrimaryKey(Long goodsId);

    int insert(Goods record);

    int insertSelective(Goods record);

    Goods selectByPrimaryKey(Long goodsId);

    int updateByPrimaryKeySelective(Goods record);

    int updateByPrimaryKeyWithBLOBs(Goods record);

    int updateByPrimaryKey(Goods record);

    List<Goods> findGoodsList(PageQueryUtil pageUtil);

    int getTotalGoods(PageQueryUtil pageUtil);

    List<Goods> selectByPrimaryKeys(List<Long> goodsIds);

    List<Goods> findGoodsListBySearch(PageQueryUtil pageUtil);

    int getTotalGoodsBySearch(PageQueryUtil pageUtil);

    int batchInsert(@Param("newBeeMallGoodsList") List<Goods> newBeeMallGoodsList);

    int updateStockNum(@Param("stockNumDTOS") List<StockNumDTO> stockNumDTOS);

    int batchUpdateSellStatus(@Param("orderIds")Long[] orderIds,@Param("sellStatus") int sellStatus);

}
