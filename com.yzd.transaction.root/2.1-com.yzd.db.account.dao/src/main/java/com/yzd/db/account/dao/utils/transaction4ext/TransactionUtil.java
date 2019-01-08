package com.yzd.db.account.dao.utils.transaction4ext;

import com.yzd.db.account.dao.utils.enum4ext.ITransactionActivityDetailStatusEnum;

public class TransactionUtil {
    private TransactionUtil() {
        throw new IllegalStateException("Utility class");
    }
    public static String getTxcBranceId(ITransactionActivityDetailStatusEnum statusEnum){
        Long transactionId=TransactionContext.getTransactionId();
        Integer status=statusEnum.getStatus();
        return transactionId+"-"+String.format("%02d",status);
    }
}
