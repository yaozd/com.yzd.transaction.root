package com.yzd.db.account.dao.utils.transaction4ext;

import com.yzd.db.account.dao.utils.enum4ext.ITransactionActivityDetailStatusEnum;

public class TransactionInfo {
    /**
     * 全局事务ID
     */
    private Long transactionId;

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    /**
     * 分支事务ID
     */
    private String transactionBranceId;

    public String getTransactionBranceId() {
        return transactionBranceId;
    }

    public void setTransactionBranceId(String transactionBranceId) {
        this.transactionBranceId = transactionBranceId;
    }

    /**
     *
     */
    private ITransactionActivityDetailStatusEnum activityDetailStatusEnum;

    public ITransactionActivityDetailStatusEnum getActivityDetailStatusEnum() {
        return activityDetailStatusEnum;
    }

    public void setActivityDetailStatusEnum(ITransactionActivityDetailStatusEnum activityDetailStatusEnum) {
        this.activityDetailStatusEnum = activityDetailStatusEnum;
    }
    private String transactionBranchDetailJson;

    public String getTransactionBranchDetailJson() {
        return transactionBranchDetailJson;
    }

    public void setTransactionBranchDetailJson(String transactionBranchDetailJson) {
        this.transactionBranchDetailJson = transactionBranchDetailJson;
    }
}
