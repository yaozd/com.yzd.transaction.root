package com.yzd.db.account.entity.table;

import java.util.Date;

public class TbTransactionActivity {
    private Long id;

    private Long txcId;

    private Integer txcActivityCode;

    private String txcActivityName;

    private Integer txcTriggerStatus;

    private Integer txcExecuteStatus;

    private String txcExecuteLog;

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

    public Integer getTxcActivityCode() {
        return txcActivityCode;
    }

    public void setTxcActivityCode(Integer txcActivityCode) {
        this.txcActivityCode = txcActivityCode;
    }

    public String getTxcActivityName() {
        return txcActivityName;
    }

    public void setTxcActivityName(String txcActivityName) {
        this.txcActivityName = txcActivityName == null ? null : txcActivityName.trim();
    }

    public Integer getTxcTriggerStatus() {
        return txcTriggerStatus;
    }

    public void setTxcTriggerStatus(Integer txcTriggerStatus) {
        this.txcTriggerStatus = txcTriggerStatus;
    }

    public Integer getTxcExecuteStatus() {
        return txcExecuteStatus;
    }

    public void setTxcExecuteStatus(Integer txcExecuteStatus) {
        this.txcExecuteStatus = txcExecuteStatus;
    }

    public String getTxcExecuteLog() {
        return txcExecuteLog;
    }

    public void setTxcExecuteLog(String txcExecuteLog) {
        this.txcExecuteLog = txcExecuteLog == null ? null : txcExecuteLog.trim();
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