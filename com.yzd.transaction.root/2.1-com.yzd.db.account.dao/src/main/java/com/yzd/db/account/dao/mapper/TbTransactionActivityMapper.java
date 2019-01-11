package com.yzd.db.account.dao.mapper;

import com.yzd.db.account.entity.table.TbTransactionActivity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TbTransactionActivityMapper {
    int insertSelective(TbTransactionActivity record);

    TbTransactionActivity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TbTransactionActivity record);

    ////////////////////////////////////////////////////////////////////
    TbTransactionActivity selectByTxcId(Long id);

    List<TbTransactionActivity> selectList(@Param("pojo") TbTransactionActivity pojo);
}