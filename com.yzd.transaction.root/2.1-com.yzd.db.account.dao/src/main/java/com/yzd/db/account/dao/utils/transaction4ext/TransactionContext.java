package com.yzd.db.account.dao.utils.transaction4ext;

import com.yzd.db.account.dao.service.inf.ITransactionActivityDetailInf;
import com.yzd.db.account.dao.utils.bean4ext.SpringContextUtil;
import com.yzd.db.account.dao.utils.enum4ext.ITransactionActivityDetailStatusEnum;
import com.yzd.db.account.dao.utils.fastjson4ext.FastJsonUtil;
import com.yzd.db.account.entity.table.TbTransactionActivityDetail;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
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
     * 事务初始化
     * 把全局的事务ID绑定到当前事务的上下文
     *
     * @param transactionId
     */
    public static void bind(Long transactionId) {
        //移除旧的事务信息
        remove();
        //
        TransactionInfo item = new TransactionInfo();
        item.setTransactionId(transactionId);
        set(item);
        log.info("主事务日志>>>" + FastJsonUtil.serialize(item));
    }

    public static void unbind() {
        //移除旧的事务信息
        remove();
    }

    public static Long getTransactionId() {
        return get().getTransactionId();
    }

    /**
     * 设置分支事务id
     *
     * @param transactionBranceId
     */
    public static void setTransactionBranceId(String transactionBranceId) {
        get().setTransactionBranceId(transactionBranceId);
    }

    public static String getTransactionBranceId() {
        return get().getTransactionBranceId();
    }

    /**
     * 绑定分支事务
     * 目前暂时事务的路由信息的配置
     *
     * @param activityDetailStatusEnum
     * @param requestArgs              事务的详细=事务的请求参数
     */
    public static void bindBranchTransaction(ITransactionActivityDetailStatusEnum activityDetailStatusEnum, Object requestArgs) {
        TransactionInfo transactionInfo = get();
        if (transactionInfo == null) {
            throw new IllegalStateException("当前事务为null,事务没有初始化!");
        }
        if (transactionInfo.getTransactionId() == null || transactionInfo.getTransactionId() == 0) {
            throw new IllegalStateException("当前事务没有找到全局的事务ID!");
        }
        transactionInfo.setActivityDetailStatusEnum(activityDetailStatusEnum);
        transactionInfo.setTransactionBranchDetailJson(FastJsonUtil.serialize(requestArgs));
    }

    /**
     *
     */
    public static void unbindBranchTransaction() {
        TransactionInfo transactionInfo = get();
        if (transactionInfo.getActivityDetailStatusEnum() == null) {
            throw new IllegalStateException("分支事务详情状态=null!");
        }
        //
        Long transactionId = transactionInfo.getTransactionId();
        String txcDetailJson = transactionInfo.getTransactionBranchDetailJson();
        ITransactionActivityDetailStatusEnum detailStatusEnum = transactionInfo.getActivityDetailStatusEnum();
        String txcBranchId = TransactionUtil.getTxcBranceId(detailStatusEnum);
        Integer txcStepState = detailStatusEnum.getStatus();
        String txcStepName = detailStatusEnum.getName();
        //
        TbTransactionActivityDetail item = new TbTransactionActivityDetail();
        item.setTxcId(transactionId);
        item.setTxcBranceId(txcBranchId);
        item.setTxcDetailJaon(txcDetailJson);
        item.setTxcStepStatus(txcStepState);
        item.setTxcStepName(txcStepName);
        item.setGmtCreate(new Date());
        item.setGmtModified(new Date());
        ITransactionActivityDetailInf iTransactionActivityDetailInf = SpringContextUtil.getInstance().getBean(ITransactionActivityDetailInf.class);
        iTransactionActivityDetailInf.insert(item);
        log.info("分支事务日志>>>" + FastJsonUtil.serialize(transactionInfo));
        //删除分支事务ID,防止事务重复使用
        transactionInfo.setActivityDetailStatusEnum(null);
    }
}
