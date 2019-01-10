package com.yzd.db.account.dao.dao;

import com.yzd.db.account.dao.base.A1BaseUnitTest;
import com.yzd.db.account.entity.table.TbAccount;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

@Slf4j
public class TbAccountDao4UnitTest extends A1BaseUnitTest {

    @Autowired
    TbAccountDao tbAccountDao;

    @Test
    public void insertSelective() {
        TbAccount item = new TbAccount();
        item.setId(0L);
        item.setUserName("yzd");
        item.setBalance(10000L);
        item.setCreateTime(new Date());
        item.setUpdateTime(new Date());
        Integer n = tbAccountDao.insertSelective(item);
        log.info(n.toString());
    }

    @Test
    public void update() {
        TbAccount item = new TbAccount();
        item.setId(3L);
        item.setUserName("yzd");
        item.setBalance(100L);
        tbAccountDao.updateSelective(item);
    }
}