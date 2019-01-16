package com.yzd.db.account.dao.service.inf;

import com.yzd.db.account.dao.base.A1BaseUnitTest;
import com.yzd.db.account.dao.utils.enum4ext.ITransactionActivityDetail4StepStatusEnum;
import com.yzd.db.account.dao.utils.enum4ext.ITransactionActivityEnum;
import com.yzd.db.account.dao.utils.fastjson4ext.FastJsonUtil;
import com.yzd.db.account.dao.utils.transaction4ext.TransactionContext;
import com.yzd.db.account.dao.utils.transaction4ext.TransactionUtil;
import com.yzd.db.account.entity.table.TbAccount;
import com.yzd.db.account.entity.table.TbTransactionActivity;
import com.yzd.db.account.entity.table.TbTransactionActivityDetail;
import com.yzd.db.account.entity.tableExt.TbAccountExt.TbAccount4Payment;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/***
 * 测试-03
 */
@Slf4j
public class T3Transaction4UnitTest extends A1BaseUnitTest {
    @Autowired
    ITransactionActivityInf iTransactionActivityInf;
    @Autowired
    ITransactionActivityDetailInf iTransactionActivityDetailInf;
    @Autowired
    IAccountInf iAccountInf;

    @Test
    public void paymentByTransactionContext() {
        //事务-转账交易-初始化
        initTransaction();
        //--------------------
        log.info("当前事务ID=" + TransactionContext.getTransactionId());
        //--------------------
        //事务-转账交易-状态更新-扣款
        Long userId = 5L;
        Long payMoney = 10L;
        TbAccount item = iAccountInf.selectById(userId);
        TbAccount4Payment itemPay = TbAccount4Payment.toTbAccout4Payment(item);
        itemPay.setPayMoney(payMoney);
        iAccountInf.payment(itemPay);
        //事务-转账交易-状态更新-扣款
        String txcDetailJaon = FastJsonUtil.serialize(itemPay);
        Long transactionId = TransactionContext.getTransactionId();
        ITransactionActivityDetail4StepStatusEnum detailStatusEnum = ITransactionActivityDetail4StepStatusEnum.TransferMoney.TRANSFER;
        String txcBranceId = TransactionUtil.getTxcBranceId(detailStatusEnum);
        Integer txcStepState = detailStatusEnum.getStatus();
        String txcStepName = detailStatusEnum.getName();
        //
        TbTransactionActivityDetail itemTxcStep01 = new TbTransactionActivityDetail();
        itemTxcStep01.setTxcId(transactionId);
        itemTxcStep01.setTxcBranceId(txcBranceId);
        itemTxcStep01.setTxcDetailJaon(txcDetailJaon);
        itemTxcStep01.setTxcStepStatus(txcStepState);
        itemTxcStep01.setTxcStepName(txcStepName);
        itemTxcStep01.setGmtCreate(new Date());
        itemTxcStep01.setGmtModified(new Date());
        iTransactionActivityDetailInf.insert(itemTxcStep01);
        log.info("扣款日志:" + txcDetailJaon);
        //--------------------
        //事务-转账交易-完成
        completeTransaction();
    }

    private void initTransaction() {
        Long transactionId = System.currentTimeMillis();
        ITransactionActivityEnum.Activities activitiy = ITransactionActivityEnum.Activities.TRANSFER_MONEY;
        ITransactionActivityEnum.TriggerStatus triggerStatus = ITransactionActivityEnum.TriggerStatus.RUNNING;
        ITransactionActivityEnum.ExecuteStatus executeStatus = ITransactionActivityEnum.ExecuteStatus.EXECUTE_RUNNING;
        //
        TbTransactionActivity item = new TbTransactionActivity();
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
        Long transactionId = TransactionContext.getTransactionId();
        TbTransactionActivity tbTransactionActivity = iTransactionActivityInf.selectByTxcId(transactionId);
        ITransactionActivityEnum.TriggerStatus triggerStatus = ITransactionActivityEnum.TriggerStatus.COMPETE;
        ITransactionActivityEnum.ExecuteStatus executeStatus = ITransactionActivityEnum.ExecuteStatus.EXECUTE_SUCCESS;
        //
        TbTransactionActivity item = new TbTransactionActivity();
        item.setId(tbTransactionActivity.getId());
        item.setTxcTriggerStatus(triggerStatus.getStatus());
        item.setTxcExecuteStatus(executeStatus.getStatus());
        item.setGmtModified(new Date());
        //
        iTransactionActivityInf.update(item);
        //
        TransactionContext.unbind();
    }
}
