package com.yzd.db.account.dao.dao;

import com.yzd.db.account.dao.mapper.TbAccountMapper;
import com.yzd.db.account.entity.table.TbAccount;
import com.yzd.db.account.entity.tableExt.TbAccountExt.TbAccount4Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class TbAccountDao {
    @Autowired
    private TbAccountMapper tbAccountMapper;

    public int insertSelective(TbAccount record) {
        return tbAccountMapper.insertSelective(record);
    }

    public int updateSelective(TbAccount record) {
        return tbAccountMapper.updateByPrimaryKeySelective(record);
    }

    public int updateByPrimaryKeyForPayment(TbAccount4Payment item) {
        return tbAccountMapper.updateByPrimaryKeyForPayment(item);
    }

    public TbAccount selectByPrimaryKey(Long id) {
        return tbAccountMapper.selectByPrimaryKey(id);
    }
}
