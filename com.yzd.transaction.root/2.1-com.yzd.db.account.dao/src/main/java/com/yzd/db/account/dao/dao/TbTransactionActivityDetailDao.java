package com.yzd.db.account.dao.dao;

import com.yzd.db.account.dao.mapper.TbTransactionActivityDetailMapper;
import com.yzd.db.account.entity.table.TbTransactionActivityDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class TbTransactionActivityDetailDao {
    @Autowired
    TbTransactionActivityDetailMapper tbTransactionActivityDetailMapper;

    public int insertSelective(TbTransactionActivityDetail record) {
        return tbTransactionActivityDetailMapper.insertSelective(record);
    }

    public TbTransactionActivityDetail selectByPrimaryKey(Long id) {
        return tbTransactionActivityDetailMapper.selectByPrimaryKey(id);
    }

    public int updateByPrimaryKeySelective(TbTransactionActivityDetail record) {
        return tbTransactionActivityDetailMapper.updateByPrimaryKeySelective(record);
    }
}
