package com.yzd.db.account.dao.service.inf;

import com.yzd.db.account.dao.base.A1BaseUnitTest;
import com.yzd.db.account.dao.utils.bean4ext.SpringContextUtil;
import com.yzd.db.account.dao.utils.enum4ext.ITransactionActivityEnum;
import com.yzd.db.account.dao.utils.transaction4ext.TransactionContext;
import com.yzd.db.account.entity.table.TbAccount;
import com.yzd.db.account.entity.tableExt.TbAccountExt.TbAccount4Payment;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/**
 * 测试-05
 */
@Slf4j
public class T5Transfer4UnitTest extends A1BaseUnitTest {
    //加载当前的上下文信息
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    IAccountInf iAccountInf;
    @Before // 在测试开始前初始化工作
    public void setup() {
        SpringContextUtil.getInstance().setCtx(applicationContext);
    }
    @Test
    public void transferByFunction(){
        //事务-转账交易-初始化
        TransactionContext.initTransaction(ITransactionActivityEnum.Activities.TRANSFER_MONEY);
        TransactionContext.executeTransaction(()->{
            //事务-转账交易-状态更新-扣款
            Long userId = 5L;
            Long payMoney = 10L;
            TbAccount item = iAccountInf.selectById(userId);
            TbAccount4Payment itemPay = TbAccount4Payment.toTbAccout4Payment(item);
            itemPay.setPayMoney(payMoney);
            //
            iAccountInf.transfer(itemPay);
            return true;
        });
    }
    @Test
    public void transfer() {
        //事务-转账交易-初始化
        TransactionContext.initTransaction(ITransactionActivityEnum.Activities.TRANSFER_MONEY);
        try {
            //
            //事务-转账交易-状态更新-扣款
            Long userId = 5L;
            Long payMoney = 10L;
            TbAccount item = iAccountInf.selectById(userId);
            TbAccount4Payment itemPay = TbAccount4Payment.toTbAccout4Payment(item);
            itemPay.setPayMoney(payMoney);
            //
            iAccountInf.transfer(itemPay);
            //
        } catch (Exception ex) {
            //异常再次对外抛出之前，必须要先记录异常，这样可以把异常定位在距离真实出错的类上，方便问题定位。
            log.error("事务异常：",ex);
            //事务-转账交易-异常
            TransactionContext.exceptionTransaction();
            throw new IllegalStateException(ex);
        }
        //事务-转账交易-完成
        TransactionContext.completeTransaction();
    }
}
