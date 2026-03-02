package zhenghc.entity;

import java.io.Serializable;
import java.math.BigDecimal;

public class SeckillMessage implements Serializable {
    private String orderNo;      // 订单号，Controller层生成
    private Long userId;         // 用户ID
    private Long activityId;     // 秒杀活动ID
    private Long cardId;         // 会员卡种ID
    private BigDecimal seckillPrice; // 秒杀价格
    private String createTime;   // 创建时间

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public BigDecimal getSeckillPrice() {
        return seckillPrice;
    }

    public void setSeckillPrice(BigDecimal seckillPrice) {
        this.seckillPrice = seckillPrice;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
