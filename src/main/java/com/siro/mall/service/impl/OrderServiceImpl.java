package com.siro.mall.service.impl;

import com.siro.mall.common.*;
import com.siro.mall.dao.GoodsMapper;
import com.siro.mall.dao.OrderItemMapper;
import com.siro.mall.dao.OrderMapper;
import com.siro.mall.dao.ShoppingCartItemMapper;
import com.siro.mall.entity.Goods;
import com.siro.mall.entity.Order;
import com.siro.mall.entity.OrderItem;
import com.siro.mall.service.OrderService;
import com.siro.mall.utils.BeanUtil;
import com.siro.mall.utils.NumberUtil;
import com.siro.mall.utils.PageQueryUtil;
import com.siro.mall.utils.PageResult;
import com.siro.mall.vo.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

/**
 * @author starsea
 * @date 2021-01-23
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderMapper orderMapper;
    @Resource
    private OrderItemMapper orderItemMapper;
    @Resource
    private ShoppingCartItemMapper shoppingCartItemMapper;
    @Resource
    private GoodsMapper goodsMapper;

    @Override
    public PageResult getOrdersPage(PageQueryUtil pageUtil) {
        List<Order> orders = orderMapper.findOrderList(pageUtil);
        int total = orderMapper.getTotalOrders(pageUtil);
        PageResult pageResult = new PageResult(orders, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String updateOrderInfo(Order MallOrder) {
        Order temp = orderMapper.selectByPrimaryKey(MallOrder.getOrderId());
        //不为空且orderStatus>=0且状态为出库之前可以修改部分信息
        if (temp != null && temp.getOrderStatus() >= 0 && temp.getOrderStatus() < 3) {
            temp.setTotalPrice(MallOrder.getTotalPrice());
            temp.setUserAddress(MallOrder.getUserAddress());
            temp.setUpdateTime(new Date());
            if (orderMapper.updateByPrimaryKeySelective(temp) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            }
            return ServiceResultEnum.DB_ERROR.getResult();
        }
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    public String checkDone(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<Order> orders = orderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (Order MallOrder : orders) {
                if (MallOrder.getIsDeleted() == 1) {
                    errorOrderNos += MallOrder.getOrderNo() + " ";
                    continue;
                }
                if (MallOrder.getOrderStatus() != 1) {
                    errorOrderNos += MallOrder.getOrderNo() + " ";
                }
            }
            if (StringUtils.isEmpty(errorOrderNos)) {
                //订单状态正常 可以执行配货完成操作 修改订单状态和更新时间
                if (orderMapper.checkDone(Arrays.asList(ids)) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行出库操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单的状态不是支付成功无法执行出库操作";
                } else {
                    return "你选择了太多状态不是支付成功的订单，无法执行配货完成操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    public String checkOut(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<Order> orders = orderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (Order MallOrder : orders) {
                if (MallOrder.getIsDeleted() == 1) {
                    errorOrderNos += MallOrder.getOrderNo() + " ";
                    continue;
                }
                if (MallOrder.getOrderStatus() != 1 && MallOrder.getOrderStatus() != 2) {
                    errorOrderNos += MallOrder.getOrderNo() + " ";
                }
            }
            if (StringUtils.isEmpty(errorOrderNos)) {
                //订单状态正常 可以执行出库操作 修改订单状态和更新时间
                if (orderMapper.checkOut(Arrays.asList(ids)) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行出库操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单的状态不是支付成功或配货完成无法执行出库操作";
                } else {
                    return "你选择了太多状态不是支付成功或配货完成的订单，无法执行出库操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    public String closeOrder(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<Order> orders = orderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (Order MallOrder : orders) {
                // isDeleted=1 一定为已关闭订单
                if (MallOrder.getIsDeleted() == 1) {
                    errorOrderNos += MallOrder.getOrderNo() + " ";
                    continue;
                }
                //已关闭或者已完成无法关闭订单
                if (MallOrder.getOrderStatus() == 4 || MallOrder.getOrderStatus() < 0) {
                    errorOrderNos += MallOrder.getOrderNo() + " ";
                }
            }
            if (StringUtils.isEmpty(errorOrderNos)) {
                //订单状态正常 可以执行关闭操作 修改订单状态和更新时间
                if (orderMapper.closeOrder(Arrays.asList(ids), MallOrderStatusEnum.ORDER_CLOSED_BY_JUDGE.getOrderStatus()) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行关闭操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单不能执行关闭操作";
                } else {
                    return "你选择的订单不能执行关闭操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    public String saveOrder(UserVO user, List<ShoppingCartItemVO> myShoppingCartItems) {
        List<Long> itemIdList = myShoppingCartItems.stream().map(ShoppingCartItemVO::getCartItemId).collect(Collectors.toList());
        List<Long> goodsIds = myShoppingCartItems.stream().map(ShoppingCartItemVO::getGoodsId).collect(Collectors.toList());
        List<Goods> MallGoods = goodsMapper.selectByPrimaryKeys(goodsIds);
        //检查是否包含已下架商品
        List<Goods> goodsListNotSelling = MallGoods.stream()
                .filter(MallGoodsTemp -> MallGoodsTemp.getGoodsSellStatus() != Constants.SELL_STATUS_UP)
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(goodsListNotSelling)) {
            //goodsListNotSelling 对象非空则表示有下架商品
            MallException.fail(goodsListNotSelling.get(0).getGoodsName() + "已下架，无法生成订单");
        }
        Map<Long, Goods> MallGoodsMap = MallGoods.stream().collect(Collectors.toMap(Goods::getGoodsId, Function.identity(), (entity1, entity2) -> entity1));
        //判断商品库存
        for (ShoppingCartItemVO shoppingCartItemVO : myShoppingCartItems) {
            //查出的商品中不存在购物车中的这条关联商品数据，直接返回错误提醒
            if (!MallGoodsMap.containsKey(shoppingCartItemVO.getGoodsId())) {
                MallException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
            }
            //存在数量大于库存的情况，直接返回错误提醒
            if (shoppingCartItemVO.getGoodsCount() > MallGoodsMap.get(shoppingCartItemVO.getGoodsId()).getStockNum()) {
                MallException.fail(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
            }
        }
        //删除购物项
        if (!CollectionUtils.isEmpty(itemIdList) && !CollectionUtils.isEmpty(goodsIds) && !CollectionUtils.isEmpty(MallGoods)) {
            if (shoppingCartItemMapper.deleteBatch(itemIdList) > 0) {
                List<StockNumDTO> stockNumDTOS = BeanUtil.copyList(myShoppingCartItems, StockNumDTO.class);
                int updateStockNumResult = goodsMapper.updateStockNum(stockNumDTOS);
                if (updateStockNumResult < 1) {
                    MallException.fail(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
                }
                //生成订单号
                String orderNo = NumberUtil.genOrderNo();
                int priceTotal = 0;
                //保存订单
                Order MallOrder = new Order();
                MallOrder.setOrderNo(orderNo);
                MallOrder.setUserId(user.getUserId());
                MallOrder.setUserAddress(user.getAddress());
                //总价
                for (ShoppingCartItemVO MallShoppingCartItemVO : myShoppingCartItems) {
                    priceTotal += MallShoppingCartItemVO.getGoodsCount() * MallShoppingCartItemVO.getSellingPrice();
                }
                if (priceTotal < 1) {
                    MallException.fail(ServiceResultEnum.ORDER_PRICE_ERROR.getResult());
                }
                MallOrder.setTotalPrice(priceTotal);
                //todo 订单body字段，用来作为生成支付单描述信息，暂时未接入第三方支付接口，故该字段暂时设为空字符串
                String extraInfo = "";
                MallOrder.setExtraInfo(extraInfo);
                //生成订单项并保存订单项纪录
                if (orderMapper.insertSelective(MallOrder) > 0) {
                    //生成所有的订单项快照，并保存至数据库
                    List<OrderItem> MallOrderItems = new ArrayList<>();
                    for (ShoppingCartItemVO MallShoppingCartItemVO : myShoppingCartItems) {
                        OrderItem MallOrderItem = new OrderItem();
                        //使用BeanUtil工具类将MallShoppingCartItemVO中的属性复制到MallOrderItem对象中
                        BeanUtil.copyProperties(MallShoppingCartItemVO, MallOrderItem);
                        //MallOrderMapper文件insert()方法中使用了useGeneratedKeys因此orderId可以获取到
                        MallOrderItem.setOrderId(MallOrder.getOrderId());
                        MallOrderItems.add(MallOrderItem);
                    }
                    //保存至数据库
                    if (orderItemMapper.insertBatch(MallOrderItems) > 0) {
                        //所有操作成功后，将订单号返回，以供Controller方法跳转到订单详情
                        return orderNo;
                    }
                    MallException.fail(ServiceResultEnum.ORDER_PRICE_ERROR.getResult());
                }
                MallException.fail(ServiceResultEnum.DB_ERROR.getResult());
            }
            MallException.fail(ServiceResultEnum.DB_ERROR.getResult());
        }
        MallException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
        return ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult();
    }

    @Override
    public OrderDetailVO getOrderDetailByOrderNo(String orderNo, Long userId) {
        Order newBeeMallOrder = orderMapper.selectByOrderNo(orderNo);
        if (newBeeMallOrder != null) {
            //todo 验证是否是当前userId下的订单，否则报错
            List<OrderItem> orderItems = orderItemMapper.selectByOrderId(newBeeMallOrder.getOrderId());
            //获取订单项数据
            if (!CollectionUtils.isEmpty(orderItems)) {
                List<OrderItemVO> newBeeMallOrderItemVOS = BeanUtil.copyList(orderItems, OrderItemVO.class);
                OrderDetailVO newBeeMallOrderDetailVO = new OrderDetailVO();
                BeanUtil.copyProperties(newBeeMallOrder, newBeeMallOrderDetailVO);
                newBeeMallOrderDetailVO.setOrderStatusString(MallOrderStatusEnum.getMallOrderStatusEnumByStatus(newBeeMallOrderDetailVO.getOrderStatus()).getName());
                newBeeMallOrderDetailVO.setPayTypeString(PayTypeEnum.getPayTypeEnumByType(newBeeMallOrderDetailVO.getPayType()).getName());
                newBeeMallOrderDetailVO.setOrderItemVOS(newBeeMallOrderItemVOS);
                return newBeeMallOrderDetailVO;
            }
        }
        return null;
    }

    @Override
    public Order getOrderByOrderNo(String orderNo) {
        return orderMapper.selectByOrderNo(orderNo);
    }

    @Override
    public PageResult getMyOrders(PageQueryUtil pageUtil) {
        int total = orderMapper.getTotalOrders(pageUtil);
        List<Order> newBeeMallOrders = orderMapper.findOrderList(pageUtil);
        List<OrderListVO> orderListVOS = new ArrayList<>();
        if (total > 0) {
            //数据转换 将实体类转成vo
            orderListVOS = BeanUtil.copyList(newBeeMallOrders, OrderListVO.class);
            //设置订单状态中文显示值
            for (OrderListVO newBeeMallOrderListVO : orderListVOS) {
                newBeeMallOrderListVO.setOrderStatusString(MallOrderStatusEnum.getMallOrderStatusEnumByStatus(newBeeMallOrderListVO.getOrderStatus()).getName());
            }
            List<Long> orderIds = newBeeMallOrders.stream().map(Order::getOrderId).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(orderIds)) {
                List<OrderItem> orderItems = orderItemMapper.selectByOrderIds(orderIds);
                Map<Long, List<OrderItem>> itemByOrderIdMap = orderItems.stream().collect(groupingBy(OrderItem::getOrderId));
                for (OrderListVO newBeeMallOrderListVO : orderListVOS) {
                    //封装每个订单列表对象的订单项数据
                    if (itemByOrderIdMap.containsKey(newBeeMallOrderListVO.getOrderId())) {
                        List<OrderItem> orderItemListTemp = itemByOrderIdMap.get(newBeeMallOrderListVO.getOrderId());
                        //将NewBeeMallOrderItem对象列表转换成NewBeeMallOrderItemVO对象列表
                        List<OrderItemVO> newBeeMallOrderItemVOS = BeanUtil.copyList(orderItemListTemp, OrderItemVO.class);
                        newBeeMallOrderListVO.setOrderItemVOS(newBeeMallOrderItemVOS);
                    }
                }
            }
        }
        PageResult pageResult = new PageResult(orderListVOS, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String cancelOrder(String orderNo, Long userId) {
        Order newBeeMallOrder = orderMapper.selectByOrderNo(orderNo);
        if (newBeeMallOrder != null) {
            //todo 验证是否是当前userId下的订单，否则报错
            //todo 订单状态判断
            if (orderMapper.closeOrder(Collections.singletonList(newBeeMallOrder.getOrderId()), MallOrderStatusEnum.ORDER_CLOSED_BY_MALLUSER.getOrderStatus()) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public String finishOrder(String orderNo, Long userId) {
        Order newBeeMallOrder = orderMapper.selectByOrderNo(orderNo);
        if (newBeeMallOrder != null) {
            //todo 验证是否是当前userId下的订单，否则报错
            //todo 订单状态判断
            newBeeMallOrder.setOrderStatus((byte) MallOrderStatusEnum.ORDER_SUCCESS.getOrderStatus());
            newBeeMallOrder.setUpdateTime(new Date());
            if (orderMapper.updateByPrimaryKeySelective(newBeeMallOrder) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public String paySuccess(String orderNo, int payType) {
        Order newBeeMallOrder = orderMapper.selectByOrderNo(orderNo);
        if (newBeeMallOrder != null) {
            //todo 订单状态判断 非待支付状态下不进行修改操作
            newBeeMallOrder.setOrderStatus((byte) MallOrderStatusEnum.OREDER_PAID.getOrderStatus());
            newBeeMallOrder.setPayType((byte) payType);
            newBeeMallOrder.setPayStatus((byte) PayStatusEnum.PAY_SUCCESS.getPayStatus());
            newBeeMallOrder.setPayTime(new Date());
            newBeeMallOrder.setUpdateTime(new Date());
            if (orderMapper.updateByPrimaryKeySelective(newBeeMallOrder) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public List<OrderItemVO> getOrderItems(Long id) {
        Order newBeeMallOrder = orderMapper.selectByPrimaryKey(id);
        if (newBeeMallOrder != null) {
            List<OrderItem> orderItems = orderItemMapper.selectByOrderId(newBeeMallOrder.getOrderId());
            //获取订单项数据
            if (!CollectionUtils.isEmpty(orderItems)) {
                List<OrderItemVO> newBeeMallOrderItemVOS = BeanUtil.copyList(orderItems, OrderItemVO.class);
                return newBeeMallOrderItemVOS;
            }
        }
        return null;
    }

    @Override
    public PayVO getOrderPay(Long orderId) {
        PayVO payVO = new PayVO();
        Order order = orderMapper.selectByPrimaryKey(orderId);
        payVO.setOrderNo(order.getOrderNo());
        //order.getTotalPrice().toString()
        payVO.setTotalAmount("12.80");

        List<OrderItem> orderItems = orderItemMapper.selectByOrderId(orderId);
        OrderItem orderItem = orderItems.get(0);
        payVO.setSubject(orderItem.getGoodsName());
        payVO.setBody(orderItem.getGoodsName());
        return payVO;
    }
}
