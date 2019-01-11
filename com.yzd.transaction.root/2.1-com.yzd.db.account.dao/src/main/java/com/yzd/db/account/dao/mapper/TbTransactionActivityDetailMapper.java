package com.yzd.db.account.dao.mapper;

import com.yzd.db.account.entity.table.TbTransactionActivityDetail;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TbTransactionActivityDetailMapper {
    int insertSelective(TbTransactionActivityDetail record);

    TbTransactionActivityDetail selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TbTransactionActivityDetail record);

    List<TbTransactionActivityDetail> selectList(@Param("pojo") TbTransactionActivityDetail pojo);
}