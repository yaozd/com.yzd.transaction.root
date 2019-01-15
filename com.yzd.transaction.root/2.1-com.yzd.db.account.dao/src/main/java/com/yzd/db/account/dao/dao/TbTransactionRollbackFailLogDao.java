package com.yzd.db.account.dao.dao;

import com.yzd.db.account.dao.mapper.TbTransactionRollbackFailLogMapper;
import com.yzd.db.account.entity.table.TbTransactionRollbackFailLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class TbTransactionRollbackFailLogDao {
    @Autowired
    TbTransactionRollbackFailLogMapper tbTransactionRollbackFailLogMapper;
    public int insertSelective(TbTransactionRollbackFailLog record){
        return tbTransactionRollbackFailLogMapper.insertSelective(record);
    }

    public int selectCount(TbTransactionRollbackFailLog record) {
        return tbTransactionRollbackFailLogMapper.selectCount(record);
    }
}
