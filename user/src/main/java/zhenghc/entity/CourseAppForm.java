package zhenghc.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class CourseAppForm {

    private Long id;

    private Long memberCourseId;

    private Long userId;

    /**
     * 申请单类型 1-退课 2-延期
     * */
    private Integer appType;

    /**
     * 申请单状态 1-待处理 2-审批通过 3-审批驳回
     * */
    private Integer status;

    /**
     * 申请单号
     * */
    private String formNum;

    /**
     * 申请原因
     * */
    private String appReason;

    /**
     * 驳回原因
     * */
    private String refuseReason;

    /**
     * 用户姓名
     * */
    private String realName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMemberCourseId() {
        return memberCourseId;
    }

    public void setMemberCourseId(Long memberCourseId) {
        this.memberCourseId = memberCourseId;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getFormNum() {
        return formNum;
    }

    public void setFormNum(String formNum) {
        this.formNum = formNum;
    }

    public String getAppReason() {
        return appReason;
    }

    public void setAppReason(String appReason) {
        this.appReason = appReason;
    }

    public String getRefuseReason() {
        return refuseReason;
    }

    public void setRefuseReason(String refuseReason) {
        this.refuseReason = refuseReason;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }
}
