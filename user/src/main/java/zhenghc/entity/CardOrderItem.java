package zhenghc.entity;

import lombok.Builder;
import zhenghc.common.BaseConstants;

import java.math.BigDecimal;

public class CardOrderItem {

    private Long id;

    private Long orderId;

    /**
     * 商品类型：1-会员卡 2-私教课
     * */
    private Integer productType;

    private Long productId;

    private String productName;

    private BigDecimal productPrice;

    /**
     * 数量
     * */
    private Integer quantity;

    /**
     * 小计金额
     * */
    private BigDecimal subtotal;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Integer getProductType() {
        return productType;
    }

    public void setProductType(Integer productType) {
        this.productType = productType;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(BigDecimal productPrice) {
        this.productPrice = productPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

}
