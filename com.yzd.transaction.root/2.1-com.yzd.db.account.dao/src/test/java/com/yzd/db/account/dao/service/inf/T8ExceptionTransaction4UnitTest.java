package com.yzd.db.account.dao.service.inf;
import java.util.Date;


import com.yzd.db.account.dao.base.A1BaseUnitTest;
import com.yzd.db.account.dao.utils.enum4ext.ITransactionActivityDetailEnum;
import com.yzd.db.account.dao.utils.enum4ext.ITransactionActivityEnum;
import com.yzd.db.account.dao.utils.enum4ext.PublicEnum;
import com.yzd.db.account.dao.utils.fastjson4ext.FastJsonUtil;
import com.yzd.db.account.entity.table.TbTransactionActivity;
import com.yzd.db.account.entity.table.TbTransactionActivityDetail;
import com.yzd.db.account.entity.table.TbTransactionRollbackFailLog;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * 测试-07
 * 处理异常的事务-回滚事务
 */
@Slf4j
public class T8ExceptionTransaction4UnitTest extends A1BaseUnitTest {
    @Autowired
    ITransactionActivityInf iTransactionActivityInf;

    @Autowired
    ITransactionActivityDetailInf iTransactionActivityDetailInf;
    @Autowired
    ITxcMessageInf iTxcMessageInf;
    @Autowired
    ITransferMoneyInf ITransferMoneyInf;

    @Autowired
    ITransactionRollbackFailLogInf iTransactionRollbackFailLogInf;

    /**
     * 处理异常的事务-回滚事务
     */
    @Test
    public void handlerForExceptionTransaction() {
        //获取异常的事务列表
        List<TbTransactionActivity> tbTransactionActivityList = getTbTransactionActivitiesForException();
        for (TbTransactionActivity item : tbTransactionActivityList) {
            try {
                doRollback4ExceptionTransaction(item);
            } catch (Exception ex) {
                log.error("事务回滚失败异常:",ex);
                //记录回滚失败的事务
                //事务回滚失败计数统计，可以通过redis做计数
                //理论上应该设计为回滚多次以上再标记为回滚异常
                //理论上应该有单独的表做回滚失败异常日志记录。
                //
                insertRollbackFailLog(item.getTxcId(), ex);
                int rollbackFailLogCount=selectCount4RollbackFailLog(item.getTxcId());
                log.info("事务回滚失败次数="+rollbackFailLogCount);
                //避免异常事务无限次执行
                if(rollbackFailLogCount>3){
                    setRollbackFail(item);
                }
            }
        }
    }

    /**
     * 查询事务回滚失败次数
     * @param txcId
     * @return
     */
    private int selectCount4RollbackFailLog(Long txcId) {
        TbTransactionRollbackFailLog record=new TbTransactionRollbackFailLog();
        record.setTxcId(txcId);
        record.setGmtIsDeleted(PublicEnum.GmtIsDeletedEnum.NO.getValue());
        return iTransactionRollbackFailLogInf.selectCount(record);
    }

    /**
     * 插入事务回滚失败日志
     * @param txcId
     * @param ex
     */
    private void insertRollbackFailLog(Long txcId, Exception ex) {
        TbTransactionRollbackFailLog record=new TbTransactionRollbackFailLog();
        record.setTxcId(txcId);
        record.setGmtCreate(new Date());
        record.setGmtModified(new Date());
        record.setFailLog(ex.getMessage());
        iTransactionRollbackFailLogInf.insertSelective(record);
    }

    /**
     * 回滚异常事务
     *
     * @param tbTransactionActivity
     */
    private void doRollback4ExceptionTransaction(TbTransactionActivity tbTransactionActivity) {
        //
        //1.获取到当前事务的访问的锁
        //
        log.info("当前主事务基本信息=" + FastJsonUtil.serialize(tbTransactionActivity));
        //2.获取到当前主事务中的分支事务列表
        List<TbTransactionActivityDetail> tbTransactionActivityDetailList = getTbTransactionActivityDetails(tbTransactionActivity.getTxcId());
        tbTransactionActivityDetailList.forEach(item -> log.info("当前分支事务基本信息" + FastJsonUtil.serialize(item)));
        //2.回滚所有分支事务流程
        boolean isCompleteRollback = rollbackAllBranchTransactionActivity(tbTransactionActivity, tbTransactionActivityDetailList);
        //如果分支事务流程回滚失败-发起盯盯预警
        //3.关闭主事务
        if (isCompleteRollback) {
            int rowCount = setRollbackComplete(tbTransactionActivity);
            if (rowCount == 0) {
                throw new IllegalStateException("主事务标识回滚完成失败！");
            }
            log.info("事务ID={},事务回滚已完成", tbTransactionActivity.getTxcId());
        }
    }

    /**
     * 主事务标记为回滚完成
     * @param tbTransactionActivity
     * @return
     */
    private int setRollbackComplete(TbTransactionActivity tbTransactionActivity) {
        TbTransactionActivity item4TbTransactionActivityUpdate = new TbTransactionActivity();
        item4TbTransactionActivityUpdate.setId(tbTransactionActivity.getId());
        item4TbTransactionActivityUpdate.setTxcTriggerStatus(ITransactionActivityEnum.TriggerStatus.COMPETE.getStatus());
        item4TbTransactionActivityUpdate.setTxcExecuteStatus(ITransactionActivityEnum.ExecuteStatus.ROLLBACK_SUCCESS.getStatus());
        item4TbTransactionActivityUpdate.setTxcExecuteLog(ITransactionActivityEnum.ExecuteStatus.ROLLBACK_SUCCESS.getDescribe());
        item4TbTransactionActivityUpdate.setGmtModified(new Date());
        return iTransactionActivityInf.update(item4TbTransactionActivityUpdate);
    }
    /**
     * 主事务标记为回滚失败,避免异常事务无限次执行
     * @param tbTransactionActivity
     * @return
     */
    private int setRollbackFail(TbTransactionActivity tbTransactionActivity) {
        TbTransactionActivity item4TbTransactionActivityUpdate = new TbTransactionActivity();
        item4TbTransactionActivityUpdate.setId(tbTransactionActivity.getId());
        item4TbTransactionActivityUpdate.setTxcTriggerStatus(ITransactionActivityEnum.TriggerStatus.COMPETE.getStatus());
        item4TbTransactionActivityUpdate.setTxcExecuteStatus(ITransactionActivityEnum.ExecuteStatus.ROLLBACK_FAIL.getStatus());
        item4TbTransactionActivityUpdate.setTxcExecuteLog(ITransactionActivityEnum.ExecuteStatus.ROLLBACK_FAIL.getDescribe());
        item4TbTransactionActivityUpdate.setGmtModified(new Date());
        return iTransactionActivityInf.update(item4TbTransactionActivityUpdate);
    }
    /**
     * 获取异常的事务列表
     *
     * @return
     */
    private List<TbTransactionActivity> getTbTransactionActivitiesForException() {
        TbTransactionActivity where4TbTransactionActivity = new TbTransactionActivity();
        where4TbTransactionActivity.setTxcTriggerStatus(ITransactionActivityEnum.TriggerStatus.RUNNING.getStatus());
        where4TbTransactionActivity.setTxcExecuteStatus(ITransactionActivityEnum.ExecuteStatus.EXECUTE_EXCEPTION.getStatus());
        where4TbTransactionActivity.setGmtIsDeleted(PublicEnum.GmtIsDeletedEnum.NO.getValue());
        return iTransactionActivityInf.selectList(where4TbTransactionActivity);
    }

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
     * 回滚所有分支事务流程
     *
     * @param tbTransactionActivity
     * @param tbTransactionActivityDetailList
     * @return
     */
    private boolean rollbackAllBranchTransactionActivity(TbTransactionActivity tbTransactionActivity, List<TbTransactionActivityDetail> tbTransactionActivityDetailList) {
        //
        Integer txcActivityCode = tbTransactionActivity.getTxcActivityCode();
        ITransactionActivityEnum.Activities activityEnum = ITransactionActivityEnum.Activities.getEnum(txcActivityCode);
        //标准的分支事务流程
        NavigableMap<Integer, String> standardStepMap = new TreeMap<Integer, String>(activityEnum.getStepMap()).descendingMap();
        for (Integer stepCode : standardStepMap.keySet()) {
            log.info("step=" + stepCode);
            Optional<TbTransactionActivityDetail> stepOptional = tbTransactionActivityDetailList.stream().filter(item -> item.getTxcStepStatus().equals(stepCode)).findFirst();
            if (BooleanUtils.isFalse(stepOptional.isPresent())) {
                log.info("step={},没有找到对应的分支事务详情", stepCode);
                continue;
            }
            TbTransactionActivityDetail stepDetail = stepOptional.orElseThrow(NullPointerException::new);
            //
            boolean isRollback = ITransferMoneyInf.rollback(stepCode, stepDetail);
            //先回滚再修改日志，保证执行顺序的。
            if (isRollback) {
                TbTransactionActivityDetail item4TbTransactionActivityDetailUpdate = new TbTransactionActivityDetail();
                item4TbTransactionActivityDetailUpdate.setId(stepDetail.getId());
                item4TbTransactionActivityDetailUpdate.setTxcRollbackStatus(ITransactionActivityDetailEnum.RollbackStatusEnum.YES.getValue());
                item4TbTransactionActivityDetailUpdate.setGmtModified(new Date());
                iTransactionActivityDetailInf.update(item4TbTransactionActivityDetailUpdate);
            }
        }
        return true;
    }
}
