package com.yzd.db.account.dao.service.impl;

import com.yzd.db.account.dao.dao.TbTransactionActivityDetailDao;
import com.yzd.db.account.dao.service.inf.ITransactionActivityDetailInf;
import com.yzd.db.account.entity.table.TbTransactionActivityDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TransactionActivityDetailImpl implements ITransactionActivityDetailInf {
    @Autowired
    TbTransactionActivityDetailDao tbTransactionActivityDetailDao;


    @Override
    public int insert(TbTransactionActivityDetail record) {
        return tbTransactionActivityDetailDao.insertSelective(record);
    }

}
