package com.yzd.db.account.entity.table;

import java.util.Date;

public class TbTransactionActivityDetail {
    private Long id;

    private Long txcId;

    private String txcBranceId;

    private Integer txcStepStatus;

    private String txcStepName;

    private String txcDetailJaon;

    private Integer txcRollbackStatus;

    private String txcLog;

    private Date gmtCreate;

    private Date gmtModified;

    private Integer gmtIsDeleted;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTxcId() {
        return txcId;
    }

    public void setTxcId(Long txcId) {
        this.txcId = txcId;
    }

    public String getTxcBranceId() {
        return txcBranceId;
    }

    public void setTxcBranceId(String txcBranceId) {
        this.txcBranceId = txcBranceId == null ? null : txcBranceId.trim();
    }

    public Integer getTxcStepStatus() {
        return txcStepStatus;
    }

    public void setTxcStepStatus(Integer txcStepStatus) {
        this.txcStepStatus = txcStepStatus;
    }

    public String getTxcStepName() {
        return txcStepName;
    }

    public void setTxcStepName(String txcStepName) {
        this.txcStepName = txcStepName == null ? null : txcStepName.trim();
    }

    public String getTxcDetailJaon() {
        return txcDetailJaon;
    }

    public void setTxcDetailJaon(String txcDetailJaon) {
        this.txcDetailJaon = txcDetailJaon == null ? null : txcDetailJaon.trim();
    }

    public Integer getTxcRollbackStatus() {
        return txcRollbackStatus;
    }

    public void setTxcRollbackStatus(Integer txcRollbackStatus) {
        this.txcRollbackStatus = txcRollbackStatus;
    }

    public String getTxcLog() {
        return txcLog;
    }

    public void setTxcLog(String txcLog) {
        this.txcLog = txcLog == null ? null : txcLog.trim();
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public Integer getGmtIsDeleted() {
        return gmtIsDeleted;
    }

    public void setGmtIsDeleted(Integer gmtIsDeleted) {
        this.gmtIsDeleted = gmtIsDeleted;
    }
}