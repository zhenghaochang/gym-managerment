package zhenghc.entity.vo;

import zhenghc.entity.MemberCourse;

import java.math.BigDecimal;
import java.util.Date;

public class MyCourseVo extends MemberCourse {

    /**
     * 课程类型 1-团课 2-私教课
     * */
    private Integer courseType;

    /**
     * 教练id
     * */
    private Long coachId;

    /**
     * 教练姓名
     * */
    private String coachName;

    /**
     * 授课时长
     * */
    private Integer duration;

    /**
     * 课程单价
     * */
    private BigDecimal price;

    /**
     * 付款时间
     * */
    private Date paymentTime;

    public Integer getCourseType() {
        return courseType;
    }

    public void setCourseType(Integer courseType) {
        this.courseType = courseType;
    }

    public Long getCoachId() {
        return coachId;
    }

    public void setCoachId(Long coachId) {
        this.coachId = coachId;
    }

    public String getCoachName() {
        return coachName;
    }

    public void setCoachName(String coachName) {
        this.coachName = coachName;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Date getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(Date paymentTime) {
        this.paymentTime = paymentTime;
    }
}
