package com.yzd.db.account.entity.table;

import java.util.Date;

public class TbTransactionActivityDetail {
    private Long id;

    private Long txcId;

    private String txcBranceId;

    private Integer txcTypeValue;

    private String txcTypeName;

    private String txcDetailJaon;

    private Integer txcState;

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

    public Integer getTxcTypeValue() {
        return txcTypeValue;
    }

    public void setTxcTypeValue(Integer txcTypeValue) {
        this.txcTypeValue = txcTypeValue;
    }

    public String getTxcTypeName() {
        return txcTypeName;
    }

    public void setTxcTypeName(String txcTypeName) {
        this.txcTypeName = txcTypeName == null ? null : txcTypeName.trim();
    }

    public String getTxcDetailJaon() {
        return txcDetailJaon;
    }

    public void setTxcDetailJaon(String txcDetailJaon) {
        this.txcDetailJaon = txcDetailJaon == null ? null : txcDetailJaon.trim();
    }

    public Integer getTxcState() {
        return txcState;
    }

    public void setTxcState(Integer txcState) {
        this.txcState = txcState;
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