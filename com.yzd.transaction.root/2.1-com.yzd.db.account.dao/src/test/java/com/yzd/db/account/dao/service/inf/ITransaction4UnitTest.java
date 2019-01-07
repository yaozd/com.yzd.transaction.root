package com.yzd.db.account.dao.service.inf;

import com.yzd.db.account.dao.base.A1BaseUnitTest;
import com.yzd.db.account.dao.utils.enum4ext.ITransactionActivityEnum;
import com.yzd.db.account.dao.utils.transaction4ext.TransactionContext;
import com.yzd.db.account.entity.table.TbTransactionActivity;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/***
 * 测试-03
 */
@Slf4j
public class ITransaction4UnitTest extends A1BaseUnitTest {
    @Autowired
    ITransactionActivityInf iTransactionActivityInf;
    @Test
    public void paymentByTransactionContext(){
        //事务-转账交易-初始化
        initTransaction();
        //--------------------
        log.info("当前事务ID="+TransactionContext.getTransactionId());
        //--------------------

        //--------------------
        //事务-转账交易-完成
        completeTransaction();
    }

    private void initTransaction() {
        Long transactionId=System.currentTimeMillis();
        ITransactionActivityEnum.Activities activitiy=ITransactionActivityEnum.Activities.TRANSFER_MONEY;
        ITransactionActivityEnum.TriggerStatus triggerStatus=ITransactionActivityEnum.TriggerStatus.RUNNING;
        ITransactionActivityEnum.ExecuteStatus executeStatus=ITransactionActivityEnum.ExecuteStatus.EXECUTE_RUNNING;
        //
        TbTransactionActivity item=new TbTransactionActivity();
        item.setTxcId(transactionId);
        item.setTxcActivityCode(activitiy.getCode());
        item.setTxcActivityName(activitiy.getName());
        item.setTxcTriggerStatus(triggerStatus.getStatus());
        item.setTxcExecuteStatus(executeStatus.getStatus());
        item.setTxcExecuteLog("");
        item.setGmtCreate(new Date());
        item.setGmtModified(new Date());
        item.setGmtIsDeleted(0);
        //
        iTransactionActivityInf.insert(item);
        //把全局的事务ID绑定到当前事务的上下文
        TransactionContext.bind(transactionId);
    }
    private void completeTransaction() {
        Long transactionId=TransactionContext.getTransactionId();
        TbTransactionActivity tbTransactionActivity=iTransactionActivityInf.selectByTxcId(transactionId);
        ITransactionActivityEnum.TriggerStatus triggerStatus=ITransactionActivityEnum.TriggerStatus.COMPETE;
        ITransactionActivityEnum.ExecuteStatus executeStatus=ITransactionActivityEnum.ExecuteStatus.EXECUTE_SUCCESS;
        //
        TbTransactionActivity item=new TbTransactionActivity();
        item.setId(tbTransactionActivity.getId());
        item.setTxcTriggerStatus(triggerStatus.getStatus());
        item.setTxcExecuteStatus(executeStatus.getStatus());
        item.setGmtModified(new Date());
        //
        iTransactionActivityInf.update(item);
        TransactionContext.unbind();
    }
}
