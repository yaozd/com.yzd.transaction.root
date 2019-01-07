package com.yzd.db.account.dao.service.inf;

import com.yzd.db.account.dao.base.A1BaseUnitTest;
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
        TbTransactionActivity item=new TbTransactionActivity();
        item.setTxcId(transactionId);
        item.setTxcTypeValue(1);
        item.setTxcTypeName("转账交易-paymentByTransactionContext");
        item.setTxcDetailJaon("");
        item.setTxcState(0);
        item.setGmtCreate(new Date());
        item.setGmtModified(new Date());
        iTransactionActivityInf.insert(item);
        //把全局的事务ID绑定到当前事务的上下文
        TransactionContext.bind(transactionId);
    }
    private void completeTransaction() {
        Long transactionId=TransactionContext.getTransactionId();
        TbTransactionActivity tbTransactionActivity=iTransactionActivityInf.selectByTxcId(transactionId);
        TbTransactionActivity item=new TbTransactionActivity();
        item.setId(tbTransactionActivity.getId());
        item.setTxcState(1);
        item.setGmtModified(new Date());
        iTransactionActivityInf.update(item);
        TransactionContext.unbind();
    }
}
