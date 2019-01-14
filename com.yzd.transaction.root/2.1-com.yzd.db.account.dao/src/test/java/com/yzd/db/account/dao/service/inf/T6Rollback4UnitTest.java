package com.yzd.db.account.dao.service.inf;
import java.util.Date;

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
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * 测试-06
 * 回滚测试
 */
@Slf4j
public class T6Rollback4UnitTest extends A1BaseUnitTest {
    @Autowired
    ITransactionActivityInf iTransactionActivityInf;

    @Autowired
    ITransactionActivityDetailInf iTransactionActivityDetailInf;
    @Autowired
    ITxcMessageInf iTxcMessageInf;
    @Autowired
    ITransferMoneyInf ITransferMoneyInf;
    /**
     * 回滚测试-针对超时的事务-模拟数据回滚。
     */
    @Test
    public void rollback4timeout() {

        TbTransactionActivity tbTransactionActivityWhere = new TbTransactionActivity();
        tbTransactionActivityWhere.setTxcTriggerStatus(ITransactionActivityEnum.TriggerStatus.RUNNING.getStatus());
        tbTransactionActivityWhere.setGmtCreate(new Date());
        tbTransactionActivityWhere.setGmtIsDeleted(PublicEnum.GmtIsDeletedEnum.NO.getValue());
        List<TbTransactionActivity> tbTransactionActivityList = iTransactionActivityInf.selectList(tbTransactionActivityWhere);
        log.info("tbTransactionActivityList count=" + tbTransactionActivityList.size());
        tbTransactionActivityList.forEach(this::doRollback);
    }

    void doRollback(TbTransactionActivity tbTransactionActivity) {
        //
        //1.获取到当前事务的访问的锁
        //
        log.info("item=" + FastJsonUtil.serialize(tbTransactionActivity));
        //2.获取到当前主事务中的分支事务列表
        List<TbTransactionActivityDetail> tbTransactionActivityDetailList = getTbTransactionActivityDetails(tbTransactionActivity.getTxcId());
        int tbTransactionActivityDetailListSize = tbTransactionActivityDetailList.size();
        log.info("tbTransactionActivityDetailList count=" + tbTransactionActivityDetailListSize);
        //情况：无效的事务:指事务只是做了初始化，并没有实际运行
        if (tbTransactionActivityDetailListSize == 0) {
            handlerForNoExistTransactionActivityDetail(tbTransactionActivity.getId());
            return;
        }
        //确认当前分支事务流程的最后一步流程是否已经被执行
        Optional<TbTransactionActivityDetail> lastStepOptional = tbTransactionActivityDetailList.stream().reduce((s1, s2) -> s1.getTxcStepStatus()>=s2.getTxcStepStatus() ? s1 : s2);
        TbTransactionActivityDetail lastStepActivity=lastStepOptional.orElseThrow( NullPointerException::new);
        boolean isExecutedLastStep=checkLastStepIsExecuted(lastStepActivity);
        if(BooleanUtils.isFalse(isExecutedLastStep)){
            //移除本地变量
            tbTransactionActivityDetailList.removeIf(item->item.getId().equals(lastStepActivity.getId()));
            //更新远程数据
            TbTransactionActivityDetail record=new TbTransactionActivityDetail();
            record.setId(lastStepActivity.getId());
            record.setTxcLog("无效的分支事务:没有找不到对应的本地事务确认日志。分支事务并没有实际运行");
            record.setGmtIsDeleted(PublicEnum.GmtIsDeletedEnum.YES.getValue());
            iTransactionActivityDetailInf.update(record);
        }

        //情况：完成事务--确认当前分支事务流程已全部执行完成
        boolean isCompleted = handlerForCheckTransactionIsCompleted(tbTransactionActivity, tbTransactionActivityDetailList);
        if (isCompleted) {
            TbTransactionActivity record=new TbTransactionActivity();
            record.setId(tbTransactionActivity.getId());
            record.setTxcTriggerStatus(ITransactionActivityEnum.TriggerStatus.COMPETE.getStatus());
            record.setTxcExecuteStatus(ITransactionActivityEnum.ExecuteStatus.EXECUTE_SUCCESS.getStatus());
            record.setTxcExecuteLog("事务已经完成【异步确认】");
            record.setGmtModified(new Date());
            record.setGmtIsDeleted(0);
            iTransactionActivityInf.update(record);
            return;
        }
        //
        //如果事务没有执行完成，标记为异常，然后再由异常事务回滚程序做统一的事务流程回滚。
        //
        //情况：回滚所有分支事务流程
        boolean isCompleteRollback= rollbackAllBranchTransactionActivity(tbTransactionActivity,tbTransactionActivityDetailList);
        //如果分支事务流程回滚失败-发起盯盯预警
        //关闭主事务
        if(isCompleteRollback){
            log.info("事务ID={},事务回滚已完成",tbTransactionActivity.getTxcId());
        }
    }

    /**
     * 回滚所有分支事务流程
     * @param tbTransactionActivity
     * @param tbTransactionActivityDetailList
     * @return
     */
    private boolean rollbackAllBranchTransactionActivity(TbTransactionActivity tbTransactionActivity,List<TbTransactionActivityDetail> tbTransactionActivityDetailList) {
        //
        Integer txcActivityCode=tbTransactionActivity.getTxcActivityCode();
        ITransactionActivityEnum.Activities activityEnum=ITransactionActivityEnum.Activities.getEnum(txcActivityCode);
        //标准的分支事务流程
        NavigableMap<Integer,String> standardStepMap=new TreeMap<Integer, String>(activityEnum.getStepMap()).descendingMap();
        for(Integer stepCode:standardStepMap.keySet()){
            log.info("step="+stepCode);
            Optional<TbTransactionActivityDetail> stepOptional=tbTransactionActivityDetailList.stream().filter(item->item.getTxcStepStatus().equals(stepCode)).findFirst();
            if(BooleanUtils.isFalse(stepOptional.isPresent())){
                log.info("step={},没有找到对应的分支事务详情",stepCode);
                continue;
            }
            TbTransactionActivityDetail stepDetail=stepOptional.orElseThrow(NullPointerException::new);
            //
            boolean isRollback= ITransferMoneyInf.rollback(stepCode,stepDetail);
            //先回滚再修改日志，保证执行顺序的。
            if(isRollback){
                TbTransactionActivityDetail item4TbTransactionActivityDetailUpdate=new TbTransactionActivityDetail();
                item4TbTransactionActivityDetailUpdate.setId(stepDetail.getId());
                item4TbTransactionActivityDetailUpdate.setTxcRollbackStatus(PublicEnum.TxcActivityDetailRollbackStatusEnum.YES.getValue());
                item4TbTransactionActivityDetailUpdate.setGmtModified(new Date());
                iTransactionActivityDetailInf.update(item4TbTransactionActivityDetailUpdate);
            }
        }
        //标记主事务--分支事务已回滚完成
        TbTransactionActivity item4TbTransactionActivityUpdate=new TbTransactionActivity();
        item4TbTransactionActivityUpdate.setId(tbTransactionActivity.getId());
        item4TbTransactionActivityUpdate.setTxcTriggerStatus(ITransactionActivityEnum.TriggerStatus.COMPETE.getStatus());
        item4TbTransactionActivityUpdate.setTxcExecuteStatus(ITransactionActivityEnum.ExecuteStatus.ROLLBACK_SUCCESS.getStatus());
        item4TbTransactionActivityUpdate.setTxcExecuteLog(ITransactionActivityEnum.ExecuteStatus.ROLLBACK_SUCCESS.getDescribe());
        item4TbTransactionActivityUpdate.setGmtModified(new Date());
        int rowCount=iTransactionActivityInf.update(item4TbTransactionActivityUpdate);
        if(rowCount>0){
            return true;
        }
        return false;
    }

    /**
     * 获取到当前主事务中的分支事务列表
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
     * 确认最后一步流程是否已经被执行
     * @param lastStepActivity 当前流程的最后一步流程
     * @return
     */
    private boolean checkLastStepIsExecuted(TbTransactionActivityDetail lastStepActivity) {
        TbTxcMessage record=new TbTxcMessage();
        record.setTxcId(lastStepActivity.getTxcId());
        record.setTxcBranceId(lastStepActivity.getTxcBranceId());
        List<TbTxcMessage> tbTxcMessageList= iTxcMessageInf.selectList(record);
        if(tbTxcMessageList.isEmpty()){
            return false;
        }
        if(tbTxcMessageList.size()>1){
            throw new IllegalStateException(String.format("全局事务ID=%s,分支事务ID=%s,当前分支事务日志执行%s次"
                    ,lastStepActivity.getTxcId()
                    ,lastStepActivity.getTxcBranceId()
                    ,tbTxcMessageList.size()));
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
        Long txcId=tbTransactionActivity.getTxcId();
        Integer txcActivityCode=tbTransactionActivity.getTxcActivityCode();
        ITransactionActivityEnum.Activities activityEnum=ITransactionActivityEnum.Activities.getEnum(txcActivityCode);
        if(activityEnum==null){
            String error=String.format("全局事务ID=%s,事务活动代码=%s,但没有找到相对应的事务活动枚举",txcId,txcActivityCode);
            log.error(error);
            throw new IllegalStateException(error);
        }
        //判断流程是否发生中断，方法：通过两个字符串在起始对比是否包含即可。eg:1,2,3,4 与1,2
        //standardStep:标准流程;currentStep:当前流程
        SortedMap<Integer,String> standardStepMap=activityEnum.getStepMap();
        String standardStepStr=StringUtils.join(standardStepMap.keySet().toArray(),",");
        log.info("标准流程="+standardStepStr);
        Set<Integer>currentStep=new TreeSet<>();
        //过滤：已经回滚的分支事务
        tbTransactionActivityDetailList.stream()
                .filter(item->item.getTxcRollbackStatus().equals(PublicEnum.GmtIsDeletedEnum.NO.getValue()))
                .forEach(item->currentStep.add(item.getTxcStepStatus()));
        String currentStepStr=StringUtils.join(currentStep.toArray(),",");
        log.info("当前流程="+currentStepStr);
        return standardStepStr.equals(currentStepStr);
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
}
