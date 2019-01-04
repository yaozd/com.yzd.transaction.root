package com.yzd.db.account.dao.mapper;

import com.yzd.db.account.entity.table.TbAccount;
import com.yzd.db.account.entity.tableExt.TbAccountExt.TbAccount4Payment;

public interface TbAccountMapper {
    int insertSelective(TbAccount record);

    TbAccount selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TbAccount record);

    int updateByPrimaryKeyForPayment(TbAccount4Payment record);
}