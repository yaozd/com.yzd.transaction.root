package com.yzd.db.account.dao.service.impl;

import com.yzd.db.account.dao.service.inf.ITransferMoneyInf;
import com.yzd.db.account.dao.utils.enum4ext.ITransactionActivityDetailStatusEnum;
import com.yzd.db.account.entity.table.TbTransactionActivityDetail;
import org.springframework.stereotype.Service;

@Service
public class TransferMoneyImpl implements ITransferMoneyInf {
    @Override
    public void rollback(Integer stepCode, TbTransactionActivityDetail stepDetail) {
        if(ITransactionActivityDetailStatusEnum.TransferMoney.TRANSFER.getStatus().equals(stepCode)){
            return;
        }
        if(ITransactionActivityDetailStatusEnum.TransferMoney.ENTER.getStatus().equals(stepCode)){
            return;
        }
    }
}
