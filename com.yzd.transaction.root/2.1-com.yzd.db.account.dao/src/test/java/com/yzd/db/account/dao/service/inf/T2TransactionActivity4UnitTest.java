package com.yzd.db.account.dao.service.inf;

import com.yzd.db.account.dao.base.A1BaseUnitTest;
import com.yzd.db.account.dao.utils.enum4ext.ITransactionActivityDetail4StepStatusEnum;
import com.yzd.db.account.dao.utils.enum4ext.ITransactionActivityEnum;
import com.yzd.db.account.dao.utils.transaction4ext.TransactionUtil;
import com.yzd.db.account.entity.table.TbTransactionActivity;
import com.yzd.db.account.entity.table.TbTransactionActivityDetail;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/***
 * 测试-02
 */
public class T2TransactionActivity4UnitTest extends A1BaseUnitTest {
    @Autowired
    ITransactionActivityInf iTransactionActivityInf;

    @Autowired
    ITransactionActivityDetailInf iTransactionActivityDetailInf;

    @Test
    public void paymentByTransaction() {
        //事务-转账交易-初始化
        Long transactionId = initTransaction();
        //---------------------------------------------------------
        ITransactionActivityDetail4StepStatusEnum activityDetailStatusEnum4Transfer = ITransactionActivityDetail4StepStatusEnum.TransferMoney.TRANSFER;
        //事务-转账交易-状态更新-扣款
        TbTransactionActivityDetail itemTxcStep01 = new TbTransactionActivityDetail();
        itemTxcStep01.setTxcId(transactionId);
        String txcBranceId4Transfer = TransactionUtil.getTxcBranceId(activityDetailStatusEnum4Transfer);
        itemTxcStep01.setTxcBranceId(txcBranceId4Transfer);
        itemTxcStep01.setTxcStepStatus(activityDetailStatusEnum4Transfer.getStatus());
        itemTxcStep01.setTxcStepName(activityDetailStatusEnum4Transfer.getName());
        itemTxcStep01.setTxcDetailJaon("扣款详情");
        itemTxcStep01.setGmtCreate(new Date());
        itemTxcStep01.setGmtModified(new Date());
        iTransactionActivityDetailInf.insert(itemTxcStep01);
        //---------------------------------------------------------
        ITransactionActivityDetail4StepStatusEnum activityDetailStatusEnum4Enter = ITransactionActivityDetail4StepStatusEnum.TransferMoney.ENTER;
        //事务-转账交易-状态更新-入账
        TbTransactionActivityDetail itemTxcStep02 = new TbTransactionActivityDetail();
        itemTxcStep02.setTxcId(transactionId);
        String txcBranceId4Enter = TransactionUtil.getTxcBranceId(activityDetailStatusEnum4Enter);
        itemTxcStep02.setTxcBranceId(txcBranceId4Enter);
        itemTxcStep01.setTxcStepStatus(activityDetailStatusEnum4Enter.getStatus());
        itemTxcStep01.setTxcStepName(activityDetailStatusEnum4Enter.getName());
        itemTxcStep02.setTxcDetailJaon("入账详情");
        itemTxcStep02.setGmtCreate(new Date());
        itemTxcStep02.setGmtModified(new Date());
        iTransactionActivityDetailInf.insert(itemTxcStep02);
        //---------------------------------------------------------
        //事务-转账交易-完成
        completeTransaction(transactionId);
    }

    private void completeTransaction(Long transactionId) {
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
    }

    private Long initTransaction() {
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
        return transactionId;
    }

}
