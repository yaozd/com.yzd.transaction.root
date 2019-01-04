package com.yzd.db.account.dao.mapper;

import com.yzd.db.account.entity.table.TbTxcMessage;

public interface TbTxcMessageMapper {
    int insertSelective(TbTxcMessage record);

    TbTxcMessage selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TbTxcMessage record);
}