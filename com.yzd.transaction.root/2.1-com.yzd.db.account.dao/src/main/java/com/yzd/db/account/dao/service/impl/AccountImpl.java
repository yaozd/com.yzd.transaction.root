package com.yzd.db.account.dao.service.impl;

import com.yzd.db.account.dao.dao.TbAccountDao;
import com.yzd.db.account.dao.dao.TbTxcMessageDao;
import com.yzd.db.account.dao.service.inf.IAccountInf;
import com.yzd.db.account.dao.utils.enum4ext.ITransactionActivityDetailStatusEnum;
import com.yzd.db.account.dao.utils.fastjson4ext.FastJsonUtil;
import com.yzd.db.account.dao.utils.transaction4ext.TransactionContext;
import com.yzd.db.account.entity.table.TbAccount;
import com.yzd.db.account.entity.table.TbTxcMessage;
import com.yzd.db.account.entity.tableExt.TbAccountExt.TbAccount4Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class AccountImpl implements IAccountInf {
    @Autowired
    TbAccountDao tbAccountDao;
    @Autowired
    TbTxcMessageDao tbTxcMessageDao;

    @Override
    public TbAccount selectById(Long id){
        return tbAccountDao.selectByPrimaryKey(id);
    }

    @Override
    public int payment(TbAccount4Payment item) {
        if(item.getBalance()<item.getPayMoney()){
            throw new IllegalStateException("扣款失败：用户余额不足");
        }
        //扣除用户余额
        int rowCount = tbAccountDao.updateByPrimaryKeyForPayment(item);
        if (item == null) {
            try {
                Thread.sleep(100000);
            } catch (InterruptedException e) {
                log.warn("Interrupted!", e);
                Thread.currentThread().interrupt();
            }
            throw new IllegalStateException("模拟异常");
        }
        if(rowCount==0){
            throw new IllegalStateException("扣款失败：操作所影响的行数row_count()=0");
        }
        String messageJson = FastJsonUtil.serialize(item);
        //记录日志-扣除用户余额
        TbTxcMessage tbTxcMessage = new TbTxcMessage();
        tbTxcMessage.setTxcId(1546482500000L);
        tbTxcMessage.setTxcTraceId(item.getId());
        tbTxcMessage.setTxcTypeValue(101);
        tbTxcMessage.setTxcTypeName("扣款");
        tbTxcMessage.setMessageJson(messageJson);
        tbTxcMessage.setGmtCreate(new Date());
        tbTxcMessage.setGmtModified(new Date());
        tbTxcMessageDao.logMessage(tbTxcMessage);
        return rowCount;
    }

    @Override
    public int transfer(TbAccount4Payment item) {
        TransactionContext.bindBranchTransaction(ITransactionActivityDetailStatusEnum.TransferMoney.TRANSFER,item);
        //
        if(System.currentTimeMillis()>100000000L) throw new IllegalStateException("模拟异常");
        //
        TransactionContext.unbindBranchTransaction();
        return 0;
    }
}
