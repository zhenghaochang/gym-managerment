package zhenghc.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;

public class Order {

    private Long id;

    private String orderNum;

    private Long userId;

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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;



}
