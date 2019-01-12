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
    /**
     * 回滚测试-针对超时的事务-模拟数据回滚。
     */
    @Test
    public void rollback4timeout() {

        TbTransactionActivity tbTransactionActivityWhere = new TbTransactionActivity();
        tbTransactionActivityWhere.setTxcTriggerStatus(ITransactionActivityEnum.TriggerStatus.RUNNING.getStatus());
        tbTransactionActivityWhere.setGmtCreate(new Date());
        tbTransactionActivityWhere.setGmtIsDeleted(0);
        List<TbTransactionActivity> tbTransactionActivityList = iTransactionActivityInf.selectList(tbTransactionActivityWhere);
        log.info("tbTransactionActivityList count=" + tbTransactionActivityList.size());
        tbTransactionActivityList.forEach(this::doRollback);
    }

    void doRollback(TbTransactionActivity tbTransactionActivity) {
        //
        //1.获取到当前事务的访问的锁
        //
        log.info("item=" + FastJsonUtil.serialize(tbTransactionActivity));
        TbTransactionActivityDetail where = new TbTransactionActivityDetail();
        where.setTxcId(tbTransactionActivity.getTxcId());
        where.setGmtIsDeleted(0);
        List<TbTransactionActivityDetail> tbTransactionActivityDetailList = iTransactionActivityDetailInf.selectList(where);
        int tbTransactionActivityDetailListSize = tbTransactionActivityDetailList.size();
        log.info("tbTransactionActivityDetailList count=" + tbTransactionActivityDetailListSize);
        //情况：无效的事务:指事务只是做了初始化，并没有实际运行
        if (tbTransactionActivityDetailListSize == 0) {
            handlerForNoExistTransactionActivityDetail(tbTransactionActivity.getId());
            return;
        }
        //确认当前流程的最后一步流程是否已经被执行
        Optional<TbTransactionActivityDetail> lastStepOptional = tbTransactionActivityDetailList.stream().reduce((s1, s2) -> s1.getTxcStepStatus()>=s2.getTxcStepStatus() ? s1 : s2);
        TbTransactionActivityDetail lastStepActivity=lastStepOptional.orElseThrow( NullPointerException::new);
        boolean isExecutedLastStep=checkLastStepIsExecuted(lastStepActivity);
        if(BooleanUtils.isFalse(isExecutedLastStep)){
            TbTransactionActivityDetail record=new TbTransactionActivityDetail();
            record.setId(lastStepActivity.getId());
            record.setTxcLog("无效的分支事务:没有找不到对应的本地事务确认日志。分支事务并没有实际运行");
            record.setGmtIsDeleted(PublicEnum.GmtIsDeletedEnum.YED.getValue());
            iTransactionActivityDetailInf.update(record);
        }
        //情况：完成事务
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
        //情况：回滚事务
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
        tbTransactionActivityDetailList.forEach(item->currentStep.add(item.getTxcStepStatus()));
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
        item.setTxcActivityName("");
        item.setTxcTriggerStatus(ITransactionActivityEnum.TriggerStatus.COMPETE.getStatus());
        item.setTxcExecuteStatus(ITransactionActivityEnum.ExecuteStatus.INVALID_TRANSACTION.getStatus());
        item.setTxcExecuteLog(ITransactionActivityEnum.ExecuteStatus.INVALID_TRANSACTION.getDescribe());
        item.setGmtModified(new Date());
        item.setGmtIsDeleted(1);
        iTransactionActivityInf.update(item);
    }
}
