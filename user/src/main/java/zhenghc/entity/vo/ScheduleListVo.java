package zhenghc.entity.vo;

import zhenghc.entity.CourseSchedule;

public class ScheduleListVo extends CourseSchedule {

    /**
     * 已预约人数
     * */
    private Integer bookedCount;

    /**
     * 当前用户是否预约对应课程
     * */
    private Boolean userBooked;

    /**
     * 预约记录id
     * */
    private Long bookingRecordId;

    /**
     * 预约记录状态
     * */
    private Integer recordStatus;


    public Integer getRecordStatus() {
        return recordStatus;
    }

    public void setRecordStatus(Integer recordStatus) {
        this.recordStatus = recordStatus;
    }

    public Long getBookingRecordId() {
        return bookingRecordId;
    }

    public void setBookingRecordId(Long bookingRecordId) {
        this.bookingRecordId = bookingRecordId;
    }

    public Boolean getUserBooked() {
        return userBooked;
    }

    public void setUserBooked(Boolean userBooked) {
        this.userBooked = userBooked;
    }

    public Integer getBookedCount() {
        return bookedCount;
    }

    public void setBookedCount(Integer bookedCount) {
        this.bookedCount = bookedCount;
    }
}
