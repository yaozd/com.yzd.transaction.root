package com.yzd.db.account.dao.service.inf;

import com.yzd.db.account.dao.base.A1BaseUnitTest;
import com.yzd.db.account.entity.table.TbTransactionActivity;
import com.yzd.db.account.entity.table.TbTransactionActivityDetail;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/***
 * 测试-02
 */
public class ITransactionActivity4UnitTest extends A1BaseUnitTest {
    @Autowired
    ITransactionActivityInf iTransactionActivityInf;

    @Autowired
    ITransactionActivityDetailInf iTransactionActivityDetailInf;

    @Test
    public void paymentByTransaction(){
        //事务-转账交易-初始化
        Long transactionId = initTransaction();
        //---------------------------------------------------------
        //事务-转账交易-状态更新-扣款
        TbTransactionActivityDetail itemTxcStep01=new TbTransactionActivityDetail();

        itemTxcStep01.setTxcId(transactionId);
        itemTxcStep01.setTxcBranceId(transactionId+"-"+"01");
        itemTxcStep01.setTxcTypeValue(1);
        itemTxcStep01.setTxcTypeName("扣款");
        itemTxcStep01.setTxcDetailJaon("扣款详情");
        itemTxcStep01.setTxcState(0);
        itemTxcStep01.setGmtCreate(new Date());
        itemTxcStep01.setGmtModified(new Date());
        iTransactionActivityDetailInf.insert(itemTxcStep01);
        //---------------------------------------------------------
        //事务-转账交易-状态更新-入账
        TbTransactionActivityDetail itemTxcStep02=new TbTransactionActivityDetail();
        itemTxcStep02.setTxcId(transactionId);
        itemTxcStep02.setTxcBranceId(transactionId+"-"+"02");
        itemTxcStep02.setTxcTypeValue(2);
        itemTxcStep02.setTxcTypeName("入账");
        itemTxcStep02.setTxcDetailJaon("入账详情");
        itemTxcStep02.setTxcState(0);
        itemTxcStep02.setGmtCreate(new Date());
        itemTxcStep02.setGmtModified(new Date());
        iTransactionActivityDetailInf.insert(itemTxcStep02);
        //---------------------------------------------------------
        //事务-转账交易-完成
        completeTransaction(transactionId);
    }
    private void completeTransaction(Long transactionId) {
        TbTransactionActivity tbTransactionActivity=iTransactionActivityInf.selectByTxcId(transactionId);
        TbTransactionActivity item=new TbTransactionActivity();
        item.setId(tbTransactionActivity.getId());
        item.setTxcState(1);
        item.setGmtModified(new Date());
        iTransactionActivityInf.update(item);
    }

    private Long initTransaction() {
        Long transactionId=System.currentTimeMillis();
        TbTransactionActivity item=new TbTransactionActivity();
        item.setTxcId(transactionId);
        item.setTxcTypeValue(1);
        item.setTxcTypeName("转账交易");
        item.setTxcDetailJaon("");
        item.setTxcState(0);
        item.setGmtCreate(new Date());
        item.setGmtModified(new Date());
        iTransactionActivityInf.insert(item);
        return transactionId;
    }

}
