package zhenghc.entity;

public class BookingPrivateRecord {

    private Long id;

    private Long userId;

    private Long memberCourseId;

    /**
     * 教练Id
     * */
    private Long coachId;

    /**
     * 期望预约时间
     * */
    private String bookingTime;

    /**
     * 状态：1-已预约 2-已签到 3-已取消
     * */
    private Integer status;

    /**
     * 备注
     * */
    private String remark;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getMemberCourseId() {
        return memberCourseId;
    }

    public void setMemberCourseId(Long memberCourseId) {
        this.memberCourseId = memberCourseId;
    }

    public Long getCoachId() {
        return coachId;
    }

    public void setCoachId(Long coachId) {
        this.coachId = coachId;
    }

    public String getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(String bookingTime) {
        this.bookingTime = bookingTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
