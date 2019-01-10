package com.yzd.db.account.dao.service.inf;

import com.yzd.db.account.dao.base.A1BaseUnitTest;
import com.yzd.db.account.dao.utils.bean4ext.SpringContextUtil;
import com.yzd.db.account.dao.utils.enum4ext.ITransactionActivityEnum;
import com.yzd.db.account.dao.utils.transaction4ext.TransactionContext;
import com.yzd.db.account.entity.table.TbAccount;
import com.yzd.db.account.entity.table.TbTransactionActivity;
import com.yzd.db.account.entity.tableExt.TbAccountExt.TbAccount4Payment;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Date;

/**
 * 测试-04
 */
@Slf4j
public class T4Transfer4UnitTest extends A1BaseUnitTest {
    //加载当前的上下文信息
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    IAccountInf iAccountInf;
    @Autowired
    ITransactionActivityInf iTransactionActivityInf;
    @Before // 在测试开始前初始化工作
    public void setup() {
        SpringContextUtil.getInstance().setCtx(applicationContext);
    }

    @Test
    public void transfer() {
        //事务-转账交易-初始化
        initTransaction(ITransactionActivityEnum.Activities.TRANSFER_MONEY);
        try {
            //
            //事务-转账交易-状态更新-扣款
            Long userId = 5L;
            Long payMoney = 10L;
            TbAccount item = iAccountInf.selectById(userId);
            TbAccount4Payment itemPay = TbAccount4Payment.toTbAccout4Payment(item);
            itemPay.setPayMoney(payMoney);
            //
            iAccountInf.payment(itemPay);
            //
        } catch (Exception ex) {
            //异常再次对外抛出之前，必须要先记录异常，这样可以把异常定位在距离真实出错的类上，方便问题定位。
            log.error("事务异常：",ex);
            //事务-转账交易-异常
            exceptionTransaction();
            throw new IllegalStateException(ex);
        }
        //事务-转账交易-完成
        completeTransaction();
    }

    /**
     * 事务初始化
     * @param currentActivities 当前事务
     */
    private void initTransaction( ITransactionActivityEnum.Activities currentActivities) {
        Long transactionId = System.currentTimeMillis();
        ITransactionActivityEnum.TriggerStatus triggerStatus = ITransactionActivityEnum.TriggerStatus.RUNNING;
        ITransactionActivityEnum.ExecuteStatus executeStatus = ITransactionActivityEnum.ExecuteStatus.EXECUTE_RUNNING;
        //
        TbTransactionActivity item = new TbTransactionActivity();
        item.setTxcId(transactionId);
        item.setTxcActivityCode(currentActivities.getCode());
        item.setTxcActivityName(currentActivities.getName());
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

    private void exceptionTransaction() {
        Long transactionId = TransactionContext.getTransactionId();
        if(transactionId==null||transactionId==0){
            throw new IllegalStateException("当前事务没有找到全局的事务ID!");
        }
        TbTransactionActivity tbTransactionActivity = iTransactionActivityInf.selectByTxcId(transactionId);
        ITransactionActivityEnum.ExecuteStatus executeStatus = ITransactionActivityEnum.ExecuteStatus.EXECUTE_EXCEPTION;
        //
        TbTransactionActivity item = new TbTransactionActivity();
        item.setId(tbTransactionActivity.getId());
        item.setTxcExecuteStatus(executeStatus.getStatus());
        item.setGmtModified(new Date());
        //
        iTransactionActivityInf.update(item);
        //
        TransactionContext.unbind();
    }
}
