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
    private Date payment_time;

}
