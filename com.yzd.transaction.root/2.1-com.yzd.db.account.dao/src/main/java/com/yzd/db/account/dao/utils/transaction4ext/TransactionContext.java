package com.yzd.db.account.dao.utils.transaction4ext;

public class TransactionContext {

    private TransactionContext() {
    }
    //
    private static final ThreadLocal<TransactionInfo> context = new ThreadLocal<>();

    public static TransactionInfo get() {
        return context.get();
    }
    public static void set(TransactionInfo item) {
        context.set(item);
    }
    public static void remove() {
        context.remove();
    }
}
