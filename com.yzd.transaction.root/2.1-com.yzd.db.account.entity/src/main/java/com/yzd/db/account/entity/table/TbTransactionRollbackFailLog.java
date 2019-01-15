package com.yzd.db.account.entity.table;

import java.util.Date;

public class TbTransactionRollbackFailLog {
    private Long id;

    private Long txcId;

    private Date gmtCreate;

    private Date gmtModified;

    private Integer gmtIsDeleted;

    private String failLog;

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

    public String getFailLog() {
        return failLog;
    }

    public void setFailLog(String failLog) {
        this.failLog = failLog == null ? null : failLog.trim();
    }
}