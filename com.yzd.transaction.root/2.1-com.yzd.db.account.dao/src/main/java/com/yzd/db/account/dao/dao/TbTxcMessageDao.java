package com.yzd.db.account.dao.dao;

import com.yzd.db.account.dao.mapper.TbTxcMessageMapper;
import com.yzd.db.account.entity.table.TbTxcMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class TbTxcMessageDao {
    @Autowired
    TbTxcMessageMapper tbTxcMessageMapper;

    public void logMessage(TbTxcMessage record) {
        tbTxcMessageMapper.insertSelective(record);
    }
}
