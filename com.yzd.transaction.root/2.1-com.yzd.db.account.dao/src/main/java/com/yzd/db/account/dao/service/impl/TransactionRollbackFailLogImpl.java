package com.yzd.db.account.dao.service.impl;

import com.yzd.db.account.dao.dao.TbTransactionRollbackFailLogDao;
import com.yzd.db.account.dao.service.inf.ITransactionRollbackFailLogInf;
import com.yzd.db.account.entity.table.TbTransactionRollbackFailLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionRollbackFailLogImpl implements ITransactionRollbackFailLogInf {

    @Autowired
    TbTransactionRollbackFailLogDao tbTransactionRollbackFailLogDao;
    @Override
    public int insertSelective(TbTransactionRollbackFailLog record) {
        return tbTransactionRollbackFailLogDao.insertSelective(record);
    }

    @Override
    public int selectCount(TbTransactionRollbackFailLog record) {
        return tbTransactionRollbackFailLogDao.selectCount(record);
    }
}
