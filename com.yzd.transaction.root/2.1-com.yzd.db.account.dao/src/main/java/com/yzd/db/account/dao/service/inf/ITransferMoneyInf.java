package com.yzd.db.account.dao.service.inf;

import com.yzd.db.account.entity.table.TbTransactionActivityDetail;

public interface ITransferMoneyInf {

    boolean rollback(Integer stepCode, TbTransactionActivityDetail stepDetail);
}
