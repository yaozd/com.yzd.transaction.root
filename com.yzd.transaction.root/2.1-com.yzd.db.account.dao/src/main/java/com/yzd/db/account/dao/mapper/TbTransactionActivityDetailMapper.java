package com.yzd.db.account.dao.mapper;

import com.yzd.db.account.entity.table.TbTransactionActivityDetail;

public interface TbTransactionActivityDetailMapper {
    int insertSelective(TbTransactionActivityDetail record);

    TbTransactionActivityDetail selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TbTransactionActivityDetail record);

}