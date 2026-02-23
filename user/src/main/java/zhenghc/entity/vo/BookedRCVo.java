package zhenghc.entity.vo;

public class BookedRCVo {

    private Long scheduleId;

    private Long bookingRecordId;

    /**
     * 1-已预约 2-申请取消预约中 3-已取消
     * */
    private Integer status;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Long getBookingRecordId() {
        return bookingRecordId;
    }

    public void setBookingRecordId(Long bookingRecordId) {
        this.bookingRecordId = bookingRecordId;
    }
}
