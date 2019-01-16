package com.yzd.db.account.dao.service.impl;

import com.yzd.db.account.dao.dao.TbAccountDao;
import com.yzd.db.account.dao.dao.TbTxcMessageDao;
import com.yzd.db.account.dao.service.inf.IAccountInf;
import com.yzd.db.account.dao.utils.enum4ext.ITransactionActivityDetail4StepStatusEnum;
import com.yzd.db.account.dao.utils.enum4ext.ITransactionActivityDetailEnum;
import com.yzd.db.account.dao.utils.enum4ext.ITxcMessageEnum;
import com.yzd.db.account.dao.utils.fastjson4ext.FastJsonUtil;
import com.yzd.db.account.dao.utils.transaction4ext.TransactionContext;
import com.yzd.db.account.dao.utils.transaction4ext.TransactionFailException;
import com.yzd.db.account.entity.table.TbAccount;
import com.yzd.db.account.entity.table.TbTxcMessage;
import com.yzd.db.account.entity.tableExt.TbAccountExt.TbAccount4Payment;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class AccountImpl implements IAccountInf {
    @Autowired
    TbAccountDao tbAccountDao;
    @Autowired
    TbTxcMessageDao tbTxcMessageDao;

    @Override
    public TbAccount selectById(Long id) {
        return tbAccountDao.selectByPrimaryKey(id);
    }

    @Override
    public int payment(@NonNull TbAccount4Payment item) {
        if (item.getBalance() < item.getPayMoney()) {
            throw new TransactionFailException("扣款失败：用户余额不足");
        }
        //扣除用户余额
        int rowCount=tbAccountDao.updateByPrimaryKeyForPayment(item);
        //模拟异常
        // MockUtil.mockSleepAndThrowException(1000L);
        //
        if (rowCount==0) {
            throw new TransactionFailException("扣款失败：操作所影响的行数row_count()=0");
        }
        //请求参数：String messageJson = FastJsonUtil.serialize(item);
        //记录日志-扣除用户余额
        Long txcId = TransactionContext.getTransactionId();
        String txcBranchId = TransactionContext.getTransactionBranceId();
        //事务的路由ID，主要是针对分库分表情况下使用，如果没有分库分表可以暂时忽略。
        String txcRouteId = item.getId().toString();
        Integer txcRollbackStatus = ITxcMessageEnum.RollbackStatus.NO.getStatus();
        TbTxcMessage tbTxcMessage = new TbTxcMessage();
        tbTxcMessage.setTxcId(txcId);
        tbTxcMessage.setTxcBranceId(txcBranchId);
        tbTxcMessage.setTxcRouteId(txcRouteId);
        tbTxcMessage.setTxcRollbackStatus(txcRollbackStatus);
        tbTxcMessage.setGmtCreate(new Date());
        tbTxcMessage.setGmtModified(new Date());
        tbTxcMessageDao.logMessage(tbTxcMessage);
        return rowCount;
    }

    /**
     * 转账-通过分布式事务
     * paymentByTransactional 与 payment 是一组。
     * 命名规则：xxxx+"ByTransactional"
     *
     * @param item
     * @return
     */
    @Override
    public int paymentByTransactional(TbAccount4Payment item) {
        TransactionContext.bindBranchTransaction(ITransactionActivityDetail4StepStatusEnum.TransferMoney.TRANSFER,ITransactionActivityDetailEnum.DatabaseNameEnum.USER_DB, item);
        //
        //业务逻辑
        payment(item);
        //模拟异常
        //ThrowUtil.mockException();
        //
        TransactionContext.unbindBranchTransaction();
        return 0;
    }

    @Override
    public boolean paymentForRollbackByTransactional(TbTxcMessage whereTxcMessage, TbAccount4Payment item4Log) {
        return paymentForRollback(whereTxcMessage,item4Log);
    }

    private boolean paymentForRollback(TbTxcMessage whereTxcMessage,TbAccount4Payment item4Log) {
        List<TbTxcMessage> tbTxcMessageList= tbTxcMessageDao.selectList(whereTxcMessage);
        if(tbTxcMessageList.isEmpty()){
            throw new IllegalStateException("数据回滚时，没有找到对应的本地事物日志！");
        }
        TbTxcMessage record=tbTxcMessageList.get(0);
        if(record.getTxcRollbackStatus().equals(ITxcMessageEnum.RollbackStatus.YES.getStatus())){
            return true;
        }
        //
        TbTxcMessage item4Update=new TbTxcMessage();
        item4Update.setId(record.getId());
        item4Update.setTxcRollbackStatus(ITxcMessageEnum.RollbackStatus.YES.getStatus());
        tbTxcMessageDao.updateByPrimaryKeySelective(item4Update);
        //
        TbAccount record4TbAccount=tbAccountDao.selectByPrimaryKey(item4Log.getId());
        if(record4TbAccount==null){
            throw new IllegalStateException(String.format("当前用户ID=%s,没有找到用户信息。",item4Log.getId()));
        }
        //扣除用户余额
        TbAccount4Payment item4UpdatePayment=TbAccount4Payment.toTbAccout4Payment(record4TbAccount);
        item4UpdatePayment.setPayMoney(-item4Log.getPayMoney());
        int rowCount=tbAccountDao.updateByPrimaryKeyForPayment(item4UpdatePayment);
        //
        if (rowCount==0) {
            throw new TransactionFailException("回滚扣款失败：操作所影响的行数row_count()=0");
        }
        log.info("数据回滚"+FastJsonUtil.serialize(item4UpdatePayment));
        return true;
    }

}
