package zhenghc.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class BookingRecord {

    private Long id;

    private Long userId;

    private Long memberCourseId;

    private Long scheduleId;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date bookingDate;

    private String bookingTime;

    private Integer status;

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
