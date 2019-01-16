package com.yzd.db.account.dao.utils.transaction4ext;

import com.yzd.db.account.dao.utils.enum4ext.ITransactionActivityDetail4StepStatusEnum;

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
    private ITransactionActivityDetail4StepStatusEnum activityDetailStatusEnum;

    public ITransactionActivityDetail4StepStatusEnum getActivityDetailStatusEnum() {
        return activityDetailStatusEnum;
    }

    public void setActivityDetailStatusEnum(ITransactionActivityDetail4StepStatusEnum activityDetailStatusEnum) {
        this.activityDetailStatusEnum = activityDetailStatusEnum;
    }

    private String transactionBranchDetailJson;

    public String getTransactionBranchDetailJson() {
        return transactionBranchDetailJson;
    }

    public void setTransactionBranchDetailJson(String transactionBranchDetailJson) {
        this.transactionBranchDetailJson = transactionBranchDetailJson;
    }

    /***
     * 分支事务所在的数据
     */
    private String transactionBranchDatabaseName;

    public String getTransactionBranchDatabaseName() {
        return transactionBranchDatabaseName;
    }

    public void setTransactionBranchDatabaseName(String transactionBranchDatabaseName) {
        this.transactionBranchDatabaseName = transactionBranchDatabaseName;
    }
}
