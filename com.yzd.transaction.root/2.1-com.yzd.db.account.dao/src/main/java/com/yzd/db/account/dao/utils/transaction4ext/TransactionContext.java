package com.yzd.db.account.dao.utils.transaction4ext;

import com.yzd.db.account.dao.service.inf.ITransactionActivityDetailInf;
import com.yzd.db.account.dao.service.inf.ITransactionActivityInf;
import com.yzd.db.account.dao.utils.bean4ext.SpringContextUtil;
import com.yzd.db.account.dao.utils.enum4ext.ITransactionActivityDetailStatusEnum;
import com.yzd.db.account.dao.utils.enum4ext.ITransactionActivityEnum;
import com.yzd.db.account.dao.utils.fastjson4ext.FastJsonUtil;
import com.yzd.db.account.entity.table.TbTransactionActivity;
import com.yzd.db.account.entity.table.TbTransactionActivityDetail;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.concurrent.Callable;

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
    //===========================================

    /**
     * 事务初始化
     *
     * @param currentActivities
     */
    public static void initTransaction(ITransactionActivityEnum.Activities currentActivities) {
        Long transactionId = System.currentTimeMillis();
        ITransactionActivityEnum.TriggerStatus triggerStatus = ITransactionActivityEnum.TriggerStatus.RUNNING;
        ITransactionActivityEnum.ExecuteStatus executeStatus = ITransactionActivityEnum.ExecuteStatus.EXECUTE_RUNNING;
        //
        TbTransactionActivity item = new TbTransactionActivity();
        item.setTxcId(transactionId);
        item.setTxcActivityCode(currentActivities.getCode());
        item.setTxcActivityName(currentActivities.getName());
        item.setTxcTriggerStatus(triggerStatus.getStatus());
        item.setTxcExecuteStatus(executeStatus.getStatus());
        item.setTxcExecuteLog("");
        item.setGmtCreate(new Date());
        item.setGmtModified(new Date());
        item.setGmtIsDeleted(0);
        //
        ITransactionActivityInf iTransactionActivityInf = SpringContextUtil.getInstance().getBean(ITransactionActivityInf.class);
        iTransactionActivityInf.insert(item);
        //
        bind(transactionId);
    }

    /**
     * 事务执行
     * @param executor
     * @param <T>
     * @return
     */
    public static  <T> T executeTransaction(Callable<T> executor){
        T result;
        try {
            result=executor.call();
        } catch (Exception ex) {
            //异常再次对外抛出之前，必须要先记录异常，这样可以把异常定位在距离真实出错的类上，方便问题定位。
            log.error("事务异常：",ex);
            //事务-转账交易-异常
            exceptionTransaction();
            throw new IllegalStateException(ex);
        }
        //事务-转账交易-完成
        completeTransaction();
        return result;
    }

    /**
     * 事务完成
     */
    public static void completeTransaction() {
        Long transactionId = getTransactionId();
        //
        ITransactionActivityInf iTransactionActivityInf = SpringContextUtil.getInstance().getBean(ITransactionActivityInf.class);
        TbTransactionActivity tbTransactionActivity = iTransactionActivityInf.selectByTxcId(transactionId);
        ITransactionActivityEnum.TriggerStatus triggerStatus = ITransactionActivityEnum.TriggerStatus.COMPETE;
        ITransactionActivityEnum.ExecuteStatus executeStatus = ITransactionActivityEnum.ExecuteStatus.EXECUTE_SUCCESS;
        //
        TbTransactionActivity item = new TbTransactionActivity();
        item.setId(tbTransactionActivity.getId());
        item.setTxcTriggerStatus(triggerStatus.getStatus());
        item.setTxcExecuteStatus(executeStatus.getStatus());
        item.setGmtModified(new Date());
        //
        iTransactionActivityInf.update(item);
        //
        unbind();
    }

    /**
     *
     */
    public static void exceptionTransaction() {
        Long transactionId = getTransactionId();
        //
        if (transactionId == null || transactionId == 0) {
            throw new IllegalStateException("当前事务没有找到全局的事务ID!");
        }
        ITransactionActivityInf iTransactionActivityInf = SpringContextUtil.getInstance().getBean(ITransactionActivityInf.class);
        TbTransactionActivity tbTransactionActivity = iTransactionActivityInf.selectByTxcId(transactionId);
        ITransactionActivityEnum.ExecuteStatus executeStatus = ITransactionActivityEnum.ExecuteStatus.EXECUTE_EXCEPTION;
        //
        TbTransactionActivity item = new TbTransactionActivity();
        item.setId(tbTransactionActivity.getId());
        item.setTxcExecuteStatus(executeStatus.getStatus());
        item.setGmtModified(new Date());
        //
        iTransactionActivityInf.update(item);
        //
        unbind();
    }
    //===========================================

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
