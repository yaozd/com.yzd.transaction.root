package com.yzd.db.account.entity.table;

import java.util.Date;

public class TbTxcMessage {
    private Long id;

    private Long txcId;

    private Long txcTraceId;

    private Integer txcTypeValue;

    private String txcTypeName;

    private String messageJson;

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

    public Long getTxcTraceId() {
        return txcTraceId;
    }

    public void setTxcTraceId(Long txcTraceId) {
        this.txcTraceId = txcTraceId;
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

    public String getMessageJson() {
        return messageJson;
    }

    public void setMessageJson(String messageJson) {
        this.messageJson = messageJson == null ? null : messageJson.trim();
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