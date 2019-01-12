package com.yzd.db.account.dao.service.impl;

import com.yzd.db.account.dao.dao.TbTxcMessageDao;
import com.yzd.db.account.dao.service.inf.ITxcMessageInf;
import com.yzd.db.account.entity.table.TbTxcMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TxcMessageImpl implements ITxcMessageInf {

    @Autowired
    TbTxcMessageDao tbTxcMessageDao;

    @Override
    public List<TbTxcMessage> selectList(TbTxcMessage record) {
        return tbTxcMessageDao.selectList(record);
    }
}
