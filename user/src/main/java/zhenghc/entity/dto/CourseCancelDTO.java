package zhenghc.entity.dto;

public class CourseCancelDTO {

    /**
     * 预约记录id
     * */
    private Long bookingRecordId;

    /**
     * 申请原因
     * */
    private String appReason;

    /**
     * 用户真实姓名
     * */
    private String realName;

    public Long getBookingRecordId() {
        return bookingRecordId;
    }

    public void setBookingRecordId(Long bookingRecordId) {
        this.bookingRecordId = bookingRecordId;
    }

    public String getAppReason() {
        return appReason;
    }

    public void setAppReason(String appReason) {
        this.appReason = appReason;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }
}
