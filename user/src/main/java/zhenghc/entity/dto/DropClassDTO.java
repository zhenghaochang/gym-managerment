package zhenghc.entity.dto;

public class DropClassDTO {

    private Long memberCourseId;

    private String appReason;

    private String realName;

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public Long getMemberCourseId() {
        return memberCourseId;
    }

    public void setMemberCourseId(Long memberCourseId) {
        this.memberCourseId = memberCourseId;
    }

    public String getAppReason() {
        return appReason;
    }

    public void setAppReason(String appReason) {
        this.appReason = appReason;
    }
}
