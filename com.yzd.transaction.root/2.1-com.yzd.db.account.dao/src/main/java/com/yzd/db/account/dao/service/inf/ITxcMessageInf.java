package com.yzd.db.account.dao.service.inf;

import com.yzd.db.account.entity.table.TbTxcMessage;

import java.util.List;

public interface ITxcMessageInf {
    List<TbTxcMessage> selectList(TbTxcMessage record);
}
