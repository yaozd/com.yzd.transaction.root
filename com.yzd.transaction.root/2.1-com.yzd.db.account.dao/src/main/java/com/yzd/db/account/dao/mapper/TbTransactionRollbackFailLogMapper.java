package com.yzd.db.account.dao.mapper;

import com.yzd.db.account.entity.table.TbTransactionRollbackFailLog;
import org.apache.ibatis.annotations.Param;

public interface TbTransactionRollbackFailLogMapper {
    int insertSelective(TbTransactionRollbackFailLog record);

    TbTransactionRollbackFailLog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TbTransactionRollbackFailLog record);

    int selectCount(@Param("pojo")TbTransactionRollbackFailLog record);
}