package com.yzd.db.account.dao.utils.transaction4ext;

/**
 * 已知情况下：
 * 事务运行失败异常
 */
public class TransactionFailException extends RuntimeException {
    /**
     * 构造一个基本异常.
     *
     * @param message 信息描述
     */
    public TransactionFailException(String message) {
        super(message);
    }
}
