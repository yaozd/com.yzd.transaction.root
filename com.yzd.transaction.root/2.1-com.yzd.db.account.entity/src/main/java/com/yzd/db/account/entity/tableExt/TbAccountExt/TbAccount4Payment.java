package com.yzd.db.account.entity.tableExt.TbAccountExt;

import com.yzd.db.account.entity.table.TbAccount;

/**
 * 付款实体类
 */
public class TbAccount4Payment extends TbAccount {
    /**
     * 支付金额
     */
    private Long payMoney;

    public Long getPayMoney() {
        return payMoney;
    }

    public void setPayMoney(Long payMoney) {
        this.payMoney = payMoney;
    }

    public static TbAccount4Payment toTbAccout4Payment(TbAccount copy){
        TbAccount4Payment item=new TbAccount4Payment();
        item.setId(copy.getId());
        item.setUserName(copy.getUserName());
        item.setBalance(copy.getBalance());
        item.setCreateTime(copy.getCreateTime());
        item.setUpdateTime(copy.getUpdateTime());
        item.setGmtTimestamp(copy.getGmtTimestamp());
        return item;
    }
}
