package zhenghc.entity;

/**
 * 课程周排班表
 * */
public class CourseSchedule {

    private Long id;

    private Long courseId;

    private Long coachId;

    /**
     * 星期几 1-周一 2-周二 ... 7-周日
     * */
    private Integer weekDay;

    /**
     * 开始时间 如09:00
     * */
    private String startTime;

    /**
     * 结束时间 如10:00
     * */
    private String endTime;

    /**
     * 上课地点
     * */
    private String location;

    /**
     * 最大预约人数
     * */
    private Integer maxCapacity;

    /**
     * 状态 1-启用 0-停用
     * */
    private Integer status;




    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getCoachId() {
        return coachId;
    }

    public void setCoachId(Long coachId) {
        this.coachId = coachId;
    }

    public Integer getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(Integer weekDay) {
        this.weekDay = weekDay;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(Integer maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
