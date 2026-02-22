package zhenghc.entity.vo;

import zhenghc.entity.MemberCourse;

public class PrivateBookingListVo extends MemberCourse {

    private String realName;

    private Integer duration;

    private Long coachId;

    public Long getCoachId() {
        return coachId;
    }

    public void setCoachId(Long coachId) {
        this.coachId = coachId;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }
}
