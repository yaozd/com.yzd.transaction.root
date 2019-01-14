package com.yzd.db.account.dao.service.inf;

import com.yzd.db.account.dao.base.A1BaseUnitTest;
import com.yzd.db.account.dao.utils.enum4ext.ITransactionActivityEnum;
import com.yzd.db.account.dao.utils.enum4ext.PublicEnum;
import com.yzd.db.account.dao.utils.fastjson4ext.FastJsonUtil;
import com.yzd.db.account.entity.table.TbTransactionActivity;
import com.yzd.db.account.entity.table.TbTransactionActivityDetail;
import com.yzd.db.account.entity.table.TbTxcMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * 测试-07
 * 处理运行超时的事务
 */
@Slf4j
public class T7TimeoutTransaction4UnitTest extends A1BaseUnitTest {

    @Autowired
    ITransactionActivityInf iTransactionActivityInf;

    @Autowired
    ITransactionActivityDetailInf iTransactionActivityDetailInf;
    @Autowired
    ITxcMessageInf iTxcMessageInf;
    @Autowired
    ITransferMoneyInf ITransferMoneyInf;

    /**
     * 处理运行超时的事务
     * PS:发版或网络抖动可能会产生次此类问题
     */
    @Test
    public void handlerForTimeoutTransaction() {
        //事务超时时间
        DateTime date4Now = new DateTime();
        Date date4Timeout = date4Now.plusMinutes(-1).toDate();
        //获取正在运行的超时事务列表
        List<TbTransactionActivity> tbTransactionActivityList = getTbTransactionActivitiesForRunningTimeout(date4Timeout);
        log.info("tbTransactionActivityList count=" + tbTransactionActivityList.size());
        tbTransactionActivityList.forEach(this::checkTransactionIsException);
    }

    /**
     * 检查当前事务是否执行异常
     *
     * @param tbTransactionActivity
     */
    void checkTransactionIsException(TbTransactionActivity tbTransactionActivity) {
        //
        //1.获取到当前事务的访问的锁
        //
        log.info("当前事务基本信息=" + FastJsonUtil.serialize(tbTransactionActivity));
        //
        //2.获取到当前主事务中的分支事务列表
        List<TbTransactionActivityDetail> tbTransactionActivityDetailList = getTbTransactionActivityDetails(tbTransactionActivity.getTxcId());
        //情况：无效的事务:指事务只是做了初始化，并没有实际运行
        if (tbTransactionActivityDetailList.isEmpty()) {
            log.info("无效的主事务:指事务只是做了初始化，并没有实际运行");
            handlerForNoExistTransactionActivityDetail(tbTransactionActivity.getId());
            return;
        }
        //确认当前分支事务流程的最后一步流程是否已经被执行
        Optional<TbTransactionActivityDetail> lastStepOptional = tbTransactionActivityDetailList.stream().reduce((s1, s2) -> s1.getTxcStepStatus() >= s2.getTxcStepStatus() ? s1 : s2);
        TbTransactionActivityDetail lastStepActivity = lastStepOptional.orElseThrow(NullPointerException::new);
        boolean isExecutedLastStep = checkLastStepIsExecuted(lastStepActivity);
        if (BooleanUtils.isFalse(isExecutedLastStep)) {
            //移除本地变量
            tbTransactionActivityDetailList.removeIf(item -> item.getId().equals(lastStepActivity.getId()));
            //更新远程数据
            TbTransactionActivityDetail record = new TbTransactionActivityDetail();
            record.setId(lastStepActivity.getId());
            record.setTxcLog("无效的分支事务:没有找不到对应的本地事务确认日志。分支事务并没有实际运行");
            record.setGmtIsDeleted(PublicEnum.GmtIsDeletedEnum.YES.getValue());
            iTransactionActivityDetailInf.update(record);
        }
        //确认当前分支事务流程已全部执行完成--分支事务流程是否完整
        boolean isCompleted = handlerForCheckTransactionIsCompleted(tbTransactionActivity, tbTransactionActivityDetailList);
        if (isCompleted) {
            TbTransactionActivity record = new TbTransactionActivity();
            record.setId(tbTransactionActivity.getId());
            record.setTxcTriggerStatus(ITransactionActivityEnum.TriggerStatus.COMPETE.getStatus());
            record.setTxcExecuteStatus(ITransactionActivityEnum.ExecuteStatus.EXECUTE_SUCCESS.getStatus());
            record.setTxcExecuteLog("分支事务流程已全部执行完成【补偿程序-异步确认】");
            record.setGmtModified(new Date());
            iTransactionActivityInf.update(record);
            return;
        }
        //如果事务没有执行完成，标记为异常事务，然后再由异常事务回滚程序做统一的事务流程回滚。
        markExceptionTransaction(tbTransactionActivity);
    }

    /**
     * 标记为异常事务
     *
     * @param tbTransactionActivity
     */
    private void markExceptionTransaction(TbTransactionActivity tbTransactionActivity) {
        TbTransactionActivity item4Update = new TbTransactionActivity();
        item4Update.setId(tbTransactionActivity.getId());
        item4Update.setTxcExecuteStatus(ITransactionActivityEnum.ExecuteStatus.EXECUTE_EXCEPTION.getStatus());
        item4Update.setGmtModified(new Date());
        iTransactionActivityInf.update(item4Update);
    }

    /**
     * 获取正在运行的超时事务列表
     *
     * @param date4Timeout
     * @return
     */
    private List<TbTransactionActivity> getTbTransactionActivitiesForRunningTimeout(Date date4Timeout) {
        TbTransactionActivity where4TbTransactionActivity = new TbTransactionActivity();
        where4TbTransactionActivity.setTxcTriggerStatus(ITransactionActivityEnum.TriggerStatus.RUNNING.getStatus());
        where4TbTransactionActivity.setTxcExecuteStatus(ITransactionActivityEnum.ExecuteStatus.EXECUTE_RUNNING.getStatus());
        where4TbTransactionActivity.setGmtCreate(date4Timeout);
        where4TbTransactionActivity.setGmtIsDeleted(PublicEnum.GmtIsDeletedEnum.NO.getValue());
        return iTransactionActivityInf.selectList(where4TbTransactionActivity);
    }
    //===========================

    /**
     * 获取到当前主事务中的分支事务列表
     *
     * @param txcId
     * @return
     */
    private List<TbTransactionActivityDetail> getTbTransactionActivityDetails(Long txcId) {
        TbTransactionActivityDetail where = new TbTransactionActivityDetail();
        where.setTxcId(txcId);
        where.setGmtIsDeleted(PublicEnum.GmtIsDeletedEnum.NO.getValue());
        return iTransactionActivityDetailInf.selectList(where);
    }

    /**
     * 处理没有分支事务详情的情况
     * PS:
     * INVALID_TRANSACTION(无效的事务):指事务只是做了初始化，并没有实际运行。
     *
     * @param id
     */
    private void handlerForNoExistTransactionActivityDetail(long id) {
        TbTransactionActivity item = new TbTransactionActivity();
        item.setId(id);
        item.setTxcTriggerStatus(ITransactionActivityEnum.TriggerStatus.COMPETE.getStatus());
        item.setTxcExecuteStatus(ITransactionActivityEnum.ExecuteStatus.INVALID_TRANSACTION.getStatus());
        item.setTxcExecuteLog(ITransactionActivityEnum.ExecuteStatus.INVALID_TRANSACTION.getDescribe());
        item.setGmtModified(new Date());
        item.setGmtIsDeleted(PublicEnum.GmtIsDeletedEnum.YES.getValue());
        iTransactionActivityInf.update(item);
    }

    /**
     * 确认最后一步流程是否已经被执行
     *
     * @param lastStepActivity 当前流程的最后一步流程
     * @return
     */
    private boolean checkLastStepIsExecuted(TbTransactionActivityDetail lastStepActivity) {
        TbTxcMessage record = new TbTxcMessage();
        record.setTxcId(lastStepActivity.getTxcId());
        record.setTxcBranceId(lastStepActivity.getTxcBranceId());
        List<TbTxcMessage> tbTxcMessageList = iTxcMessageInf.selectList(record);
        if (tbTxcMessageList.isEmpty()) {
            return false;
        }
        if (tbTxcMessageList.size() > 1) {
            throw new IllegalStateException(String.format("全局事务ID=%s,分支事务ID=%s,当前分支事务日志执行%s次"
                    , lastStepActivity.getTxcId()
                    , lastStepActivity.getTxcBranceId()
                    , tbTxcMessageList.size()));
        }
        return true;
    }

    /**
     * 检查当前事务是已经完成
     *
     * @param tbTransactionActivity
     * @param tbTransactionActivityDetailList
     * @return
     */
    private boolean handlerForCheckTransactionIsCompleted(TbTransactionActivity tbTransactionActivity, List<TbTransactionActivityDetail> tbTransactionActivityDetailList) {
        if (tbTransactionActivity.getTxcExecuteStatus().equals(ITransactionActivityEnum.ExecuteStatus.EXECUTE_EXCEPTION.getStatus())) {
            return false;
        }
        Long txcId = tbTransactionActivity.getTxcId();
        Integer txcActivityCode = tbTransactionActivity.getTxcActivityCode();
        ITransactionActivityEnum.Activities activityEnum = ITransactionActivityEnum.Activities.getEnum(txcActivityCode);
        if (activityEnum == null) {
            String error = String.format("全局事务ID=%s,事务活动代码=%s,但没有找到相对应的事务活动枚举", txcId, txcActivityCode);
            log.error(error);
            throw new IllegalStateException(error);
        }
        //判断流程是否发生中断，方法：通过两个字符串在起始对比是否包含即可。eg:1,2,3,4 与1,2
        //standardStep:标准流程;currentStep:当前流程
        SortedMap<Integer, String> standardStepMap = activityEnum.getStepMap();
        String standardStepStr = StringUtils.join(standardStepMap.keySet().toArray(), ",");
        log.info("标准流程=" + standardStepStr);
        Set<Integer> currentStep = new TreeSet<>();
        //过滤：已经回滚的分支事务
        tbTransactionActivityDetailList.stream()
                .filter(item -> item.getTxcRollbackStatus().equals(PublicEnum.GmtIsDeletedEnum.NO.getValue()))
                .forEach(item -> currentStep.add(item.getTxcStepStatus()));
        String currentStepStr = StringUtils.join(currentStep.toArray(), ",");
        log.info("当前流程=" + currentStepStr);
        return standardStepStr.equals(currentStepStr);
    }
}
