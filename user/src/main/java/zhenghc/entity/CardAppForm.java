package zhenghc.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Timestamp;

public class CardAppForm {

    /**
     * 申请单id
     * */
    private Long id;

    /**
     * 申请单号
     * */
    private String formNum;

    /**
     * 卡号
     * */
    private String cardNum;


    /**
     * 申请单类型 1-停卡 2-转卡
     * */
    private Integer appType;

    /**
     * 申请单状态 1-待处理 2-审批通过 3-审批驳回
     * */
    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp createTime;

    /**
     * 申请原因
     * */
    private String appReason;

    /**
     * 用户姓名
     * */
    private String realName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public String getFormNum() {
        return formNum;
    }

    public void setFormNum(String formNum) {
        this.formNum = formNum;
    }

    public String getCardNum() {
        return cardNum;
    }

    public void setCardNum(String cardNum) {
        this.cardNum = cardNum;
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
