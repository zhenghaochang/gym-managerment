package zhenghc.entity.vo;

import zhenghc.entity.CardAppForm;

import java.util.List;

public class AppListVo {

    /**
     * 审批单列表
     * */
    private List<CardAppForm> formList;

    /**
     * 统计总数
     * */
    private Integer total;

    /**
     * 待审批数量
     */
    private Integer pendingCount;


    public List<CardAppForm> getFormList() {
        return formList;
    }

    public void setFormList(List<CardAppForm> formList) {
        this.formList = formList;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getPendingCount() {
        return pendingCount;
    }

    public void setPendingCount(Integer pendingCount) {
        this.pendingCount = pendingCount;
    }
}
