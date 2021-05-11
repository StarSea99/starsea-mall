package com.siro.mall.controller.mall;

import com.siro.mall.common.Constants;
import com.siro.mall.common.MallException;
import com.siro.mall.common.ServiceResultEnum;
import com.siro.mall.entity.Order;
import com.siro.mall.service.OrderService;
import com.siro.mall.service.ShoppingCartService;
import com.siro.mall.utils.PageQueryUtil;
import com.siro.mall.utils.Result;
import com.siro.mall.utils.ResultGenerator;
import com.siro.mall.vo.OrderDetailVO;
import com.siro.mall.vo.ShoppingCartItemVO;
import com.siro.mall.vo.UserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * @author starsea
 * @date 2021-01-23
 */
@Api(tags = "前台订单controller")
@Controller
public class OrderController {

    @Resource
    private OrderService orderService;
    @Resource
    private ShoppingCartService shoppingCartService;

    @ApiOperation(value = "保存订单")
    @GetMapping("/saveOrder")
    public String saveOrder(HttpSession httpSession) {
        UserVO user = (UserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        List<ShoppingCartItemVO> myShoppingCartItems = shoppingCartService.getMyShoppingCartItems(user.getUserId());
        if (StringUtils.isEmpty(user.getAddress().trim())) {
            //无收货地址
            MallException.fail(ServiceResultEnum.NULL_ADDRESS_ERROR.getResult());
        }
        if (CollectionUtils.isEmpty(myShoppingCartItems)) {
            //购物车中无数据则跳转至错误页
            MallException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
        }
        //保存订单并返回订单号
        String saveOrderResult = orderService.saveOrder(user, myShoppingCartItems);
        //跳转到订单详情页
        return "redirect:/orders/" + saveOrderResult;
    }

    @ApiOperation(value = "根据订单号查询订单")
    @GetMapping("/orders/{orderNo}")
    public String orderDetailPage(HttpServletRequest request,
                                  @PathVariable("orderNo") String orderNo,
                                  HttpSession httpSession) {
        UserVO user = (UserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        OrderDetailVO orderDetailVO = orderService.getOrderDetailByOrderNo(orderNo, user.getUserId());
        if (orderDetailVO == null) {
            return "error/error_5xx";
        }
        request.setAttribute("orderDetailVO", orderDetailVO);
        return "mall/order-detail";
    }

    @ApiOperation(value = "去支付付款页面")
    @GetMapping("/selectPayType")
    public String selectPayType(HttpServletRequest request,
                                @RequestParam("orderNo") String orderNo,
                                HttpSession httpSession) {
        //UserVO user = (UserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        Order newBeeMallOrder = orderService.getOrderByOrderNo(orderNo);
        //todo 判断订单userId
        //todo 判断订单状态
        request.setAttribute("orderNo", orderNo);
        request.setAttribute("totalPrice", newBeeMallOrder.getTotalPrice());
        return "mall/pay-select";
    }

    @ApiOperation(value = "选择支付方式进行支付")
    @GetMapping("/payPage")
    public String payOrder(HttpServletRequest request,
                           @RequestParam("orderNo") String orderNo,
                           HttpSession httpSession,
                           @RequestParam("payType") int payType) {
        //UserVO user = (UserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        Order newBeeMallOrder = orderService.getOrderByOrderNo(orderNo);
        //todo 判断订单userId
        //todo 判断订单状态
        request.setAttribute("orderNo", orderNo);
        request.setAttribute("totalPrice", newBeeMallOrder.getTotalPrice());
        if (payType == 1) {
            return "mall/alipay";
        } else {
            return "mall/wxpay";
        }
    }

    @ApiOperation(value = "支付成功")
    @GetMapping("/paySuccess")
    @ResponseBody
    public Result paySuccess(@RequestParam("orderNo") String orderNo, @RequestParam("payType") int payType) {
        String payResult = orderService.paySuccess(orderNo, payType);
        if (ServiceResultEnum.SUCCESS.getResult().equals(payResult)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(payResult);
        }
    }

    @GetMapping("/myorders.html")
    public String payAliSuccess(){

        return "my-orders";
    }

    @ApiOperation(value = "手动取消订单")
    @PutMapping("/orders/{orderNo}/cancel")
    @ResponseBody
    public Result cancelOrder(@PathVariable("orderNo") String orderNo, HttpSession httpSession) {
        UserVO user = (UserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        String cancelOrderResult = orderService.cancelOrder(orderNo, user.getUserId());
        if (ServiceResultEnum.SUCCESS.getResult().equals(cancelOrderResult)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(cancelOrderResult);
        }
    }

    @ApiOperation(value = "查看我的订单列表")
    @GetMapping("/orders")
    public String orderListPage(@RequestParam Map<String, Object> params,
                                HttpServletRequest request,
                                HttpSession httpSession) {
        UserVO user = (UserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        params.put("userId", user.getUserId());
        if (StringUtils.isEmpty(params.get("page"))) {
            params.put("page", 1);
        }
        params.put("limit", Constants.ORDER_SEARCH_PAGE_LIMIT);
        //封装我的订单数据
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        request.setAttribute("orderPageResult", orderService.getMyOrders(pageUtil));
        request.setAttribute("path", "orders");
        return "mall/my-orders";
    }

    @ApiOperation(value = "确认订单到货")
    @PutMapping("/orders/{orderNo}/finish")
    @ResponseBody
    public Result finishOrder(@PathVariable("orderNo") String orderNo, HttpSession httpSession) {
        UserVO user = (UserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        String finishOrderResult = orderService.finishOrder(orderNo, user.getUserId());
        if (ServiceResultEnum.SUCCESS.getResult().equals(finishOrderResult)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(finishOrderResult);
        }
    }

}
