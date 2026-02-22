package zhenghc.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class BookingDTO {

    private Long memberCourseId;

    private Long userId;

    private Long scheduleId;

    /**
     * 预约的具体日期（如2026-02-23）
     * */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date bookingDate;

    /**
     * 预约时间（yyyy-MM-dd HH:mm:ss）
     * */
    private String bookingTime;

    /**
     * 状态：1-已预约 2-已签到 3-已取消
     * */
    private Integer status;

    public Long getMemberCourseId() {
        return memberCourseId;
    }

    public void setMemberCourseId(Long memberCourseId) {
        this.memberCourseId = memberCourseId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
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
}
