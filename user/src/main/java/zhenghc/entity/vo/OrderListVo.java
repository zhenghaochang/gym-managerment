package zhenghc.entity.vo;

import zhenghc.entity.Order;

public class OrderListVo extends Order {

    private String productName;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}
