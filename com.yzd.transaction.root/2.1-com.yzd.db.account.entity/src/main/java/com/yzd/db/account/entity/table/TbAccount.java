package com.yzd.db.account.entity.table;

import java.util.Date;

public class TbAccount {
    private Long id;

    private String userName;

    private Long balance;

    private Date createTime;

    private Date updateTime;

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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getGmtTimestamp() {
        return gmtTimestamp;
    }

    public void setGmtTimestamp(Date gmtTimestamp) {
        this.gmtTimestamp = gmtTimestamp;
    }
}