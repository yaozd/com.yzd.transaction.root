package com.yzd.db.account.entity.table;

import java.util.Date;

public class TbTxcMessage {
    private Long id;

    private Long txcId;

    private String txcBranceId;

    private String txcRouteId;

    private Integer txcRollbackStatus;

    private Date gmtCreate;

    private Date gmtModified;

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

    public String getTxcRouteId() {
        return txcRouteId;
    }

    public void setTxcRouteId(String txcRouteId) {
        this.txcRouteId = txcRouteId == null ? null : txcRouteId.trim();
    }

    public Integer getTxcRollbackStatus() {
        return txcRollbackStatus;
    }

    public void setTxcRollbackStatus(Integer txcRollbackStatus) {
        this.txcRollbackStatus = txcRollbackStatus;
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
}