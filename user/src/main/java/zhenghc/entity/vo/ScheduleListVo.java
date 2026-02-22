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
