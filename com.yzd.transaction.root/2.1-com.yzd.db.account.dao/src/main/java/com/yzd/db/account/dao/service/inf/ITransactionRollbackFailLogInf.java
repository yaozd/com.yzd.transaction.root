package com.yzd.db.account.dao.service.inf;

import com.yzd.db.account.entity.table.TbTransactionRollbackFailLog;

public interface ITransactionRollbackFailLogInf {
    int insertSelective(TbTransactionRollbackFailLog record);

    int selectCount(TbTransactionRollbackFailLog record);
}
