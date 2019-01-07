package com.yzd.db.account.dao.utils.transaction4ext;

public class TransactionContext {

    private TransactionContext() {
    }

    //
    private static final ThreadLocal<TransactionInfo> context = new ThreadLocal<>();

    private static TransactionInfo get() {
        return context.get();
    }

    private static void set(TransactionInfo item) {
        context.set(item);
    }

    private static void remove() {
        context.remove();
    }

    //===========================================

    /**
     * 把全局的事务ID绑定到当前事务的上下文
     * @param transactionId
     */
    public static void bind(Long transactionId) {
        //移除旧的事务信息
        remove();
        //
        TransactionInfo item = new TransactionInfo();
        item.setTransactionId(transactionId);
        set(item);
    }

    public static void unbind() {
        //移除旧的事务信息
        remove();
    }

    public static Long getTransactionId() {
        return get().getTransactionId();
    }
}
