package com.yzd.db.account.entity.table;

import java.util.Date;

public class TbAccount {
    private Long id;

    private String userName;

    private Long balance;

    private Date gmtCreate;

    private Date gmtModified;

    private Date gmtTimestamp;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
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

    public Date getGmtTimestamp() {
        return gmtTimestamp;
    }

    public void setGmtTimestamp(Date gmtTimestamp) {
        this.gmtTimestamp = gmtTimestamp;
    }
}