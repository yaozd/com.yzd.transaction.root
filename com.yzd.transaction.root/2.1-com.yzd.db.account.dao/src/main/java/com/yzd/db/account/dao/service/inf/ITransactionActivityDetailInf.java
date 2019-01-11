package com.yzd.db.account.dao.service.inf;

import com.yzd.db.account.entity.table.TbTransactionActivityDetail;

import java.util.List;

public interface ITransactionActivityDetailInf {

    int insert(TbTransactionActivityDetail record);

    List<TbTransactionActivityDetail> selectList(TbTransactionActivityDetail record);
}
