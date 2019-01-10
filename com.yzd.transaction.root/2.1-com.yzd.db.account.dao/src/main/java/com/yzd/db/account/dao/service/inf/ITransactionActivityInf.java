package com.yzd.db.account.dao.service.inf;

import com.yzd.db.account.entity.table.TbTransactionActivity;

public interface ITransactionActivityInf {
    int insert(TbTransactionActivity record);

    int update(TbTransactionActivity record);

    TbTransactionActivity selectByTxcId(Long id);
}
