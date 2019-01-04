package com.yzd.db.account.dao.mapper;

import com.yzd.db.account.entity.table.TbTransactionActivity;

public interface TbTransactionActivityMapper {
    int insertSelective(TbTransactionActivity record);

    TbTransactionActivity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TbTransactionActivity record);

    ////////////////////////////////////////////////////////////////////
    TbTransactionActivity selectByTxcId(Long id);
}