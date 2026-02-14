package zhenghc.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import zhenghc.entity.MemberCourse;
import zhenghc.entity.Order;

import java.math.BigDecimal;
import java.util.Date;

public class MemberCourseDTO extends MemberCourse {

    private Long id;

    private String orderNum;

//    private Long userId;

    /**
     * 订单金额
     * */
    private BigDecimal orderAmount;

    /**
     * 实付金额
     * */
    private BigDecimal paymentAmount;

    /**
     * 支付方式：1-支付宝 2-微信
     * */
    private Integer paymentMethod;

    /**
     * 支付状态：1-待支付 2-支付成功 3-支付失败 4-已退款
     * */
    private Integer paymentStatus;

    /**
     * 订单状态：1-待处理 2-已完成 3-已取消
     * */
    private Integer orderStatus;

    /**
     * 支付时间
     * */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date paymentTime;

    /**
     * 商品单价
     * */
    private BigDecimal productPrice;

    private Integer quantity;

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

//    @Override
//    public Long getUserId() {
//        return userId;
//    }
//
//    @Override
//    public void setUserId(Long userId) {
//        this.userId = userId;
//    }

    public BigDecimal getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(BigDecimal orderAmount) {
        this.orderAmount = orderAmount;
    }

    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public Integer getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(Integer paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Integer getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(Integer paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Date getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(Date paymentTime) {
        this.paymentTime = paymentTime;
    }

    public BigDecimal getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(BigDecimal productPrice) {
        this.productPrice = productPrice;
    }
}
