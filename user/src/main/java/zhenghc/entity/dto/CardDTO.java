package zhenghc.entity.dto;

import java.math.BigDecimal;

public class CardDTO {

    private Long userId;

    private Long cardId;


    /**
     * 支付方式 1-支付宝 2-微信
     * */
    private Integer paymentMethod;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public Integer getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(Integer paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
