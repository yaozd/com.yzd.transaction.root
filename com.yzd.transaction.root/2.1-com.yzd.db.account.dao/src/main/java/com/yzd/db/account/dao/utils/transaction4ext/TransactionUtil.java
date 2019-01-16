package com.yzd.db.account.dao.utils.transaction4ext;

import com.yzd.db.account.dao.utils.enum4ext.ITransactionActivityDetail4StepStatusEnum;

public class TransactionUtil {
    private TransactionUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static String getTxcBranceId(ITransactionActivityDetail4StepStatusEnum statusEnum) {
        Long transactionId = TransactionContext.getTransactionId();
        Integer status = statusEnum.getStatus();
        return transactionId + "-" + String.format("%02d", status);
    }
}
