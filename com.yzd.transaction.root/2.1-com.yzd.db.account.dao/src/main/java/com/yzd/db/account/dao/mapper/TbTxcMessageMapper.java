package com.yzd.db.account.dao.mapper;

import com.yzd.db.account.entity.table.TbTxcMessage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TbTxcMessageMapper {
    int insertSelective(TbTxcMessage record);

    TbTxcMessage selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TbTxcMessage record);

    List<TbTxcMessage> selectList(@Param("pojo") TbTxcMessage pojo);
}