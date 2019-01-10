package com.yzd.db.account.dao.dao;

import com.yzd.db.account.dao.mapper.TbTransactionActivityMapper;
import com.yzd.db.account.entity.table.TbTransactionActivity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class TbTransactionActivityDao {
    @Autowired
    TbTransactionActivityMapper tbTransactionActivityMapper;

    public int insertSelective(TbTransactionActivity record) {
        return tbTransactionActivityMapper.insertSelective(record);
    }

    public int updateByPrimaryKeySelective(TbTransactionActivity record) {
        return tbTransactionActivityMapper.updateByPrimaryKeySelective(record);
    }

    public TbTransactionActivity selectByTxcId(Long id) {
        return tbTransactionActivityMapper.selectByTxcId(id);
    }
}
