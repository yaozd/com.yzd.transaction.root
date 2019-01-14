package com.yzd.db.account.dao.service.impl;
import java.util.Date;

import com.yzd.db.account.dao.service.inf.IAccountInf;
import com.yzd.db.account.dao.service.inf.ITransferMoneyInf;
import com.yzd.db.account.dao.utils.enum4ext.ITransactionActivityDetailStatusEnum;
import com.yzd.db.account.dao.utils.fastjson4ext.FastJsonUtil;
import com.yzd.db.account.entity.table.TbTransactionActivityDetail;
import com.yzd.db.account.entity.table.TbTxcMessage;
import com.yzd.db.account.entity.tableExt.TbAccountExt.TbAccount4Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransferMoneyImpl implements ITransferMoneyInf {
    @Autowired
    IAccountInf accountInf;

    @Override
    public boolean rollback(Integer stepCode, TbTransactionActivityDetail stepDetail) {
        TbTxcMessage whereTxcMessage=new TbTxcMessage();
        whereTxcMessage.setTxcId(stepDetail.getTxcId());
        whereTxcMessage.setTxcBranceId(stepDetail.getTxcBranceId());
        //
        if(ITransactionActivityDetailStatusEnum.TransferMoney.TRANSFER.getStatus().equals(stepCode)){
            TbAccount4Payment item=FastJsonUtil.deserialize(stepDetail.getTxcDetailJaon(),TbAccount4Payment.class);
            return accountInf.paymentForRollbackByTransactional(whereTxcMessage,item);
        }
        if(ITransactionActivityDetailStatusEnum.TransferMoney.ENTER.getStatus().equals(stepCode)){
            return false;
        }
        return false;
    }
}
