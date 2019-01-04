package com.yzd.db.account.dao.service.impl;

import com.yzd.db.account.dao.dao.TbTransactionActivityDao;
import com.yzd.db.account.dao.service.inf.ITransactionActivityInf;
import com.yzd.db.account.entity.table.TbTransactionActivity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TransactionActivityImpl implements ITransactionActivityInf {

    @Autowired
    TbTransactionActivityDao tbTransactionActivityDao;

    @Override
    public int insert(TbTransactionActivity record) {
        return tbTransactionActivityDao.insertSelective(record);
    }

    @Override
    public int update(TbTransactionActivity record) {
        return tbTransactionActivityDao.updateByPrimaryKeySelective(record);
    }

    @Override
    public TbTransactionActivity selectByTxcId(Long id) {
        return tbTransactionActivityDao.selectByTxcId(id);
    }
}
