package com.yzd.db.account.dao.service.inf;

import com.yzd.db.account.dao.base.A1BaseUnitTest;
import com.yzd.db.account.dao.utils.enum4ext.ITransactionActivityEnum;
import com.yzd.db.account.dao.utils.fastjson4ext.FastJsonUtil;
import com.yzd.db.account.entity.table.TbTransactionActivity;
import com.yzd.db.account.entity.table.TbTransactionActivityDetail;
import lombok.extern.slf4j.Slf4j;
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
        //情况：无效事务
        if (tbTransactionActivityDetailListSize == 0) {
            handlerForNoExistTransactionActivityDetail(tbTransactionActivity.getId());
            return;
        }
        //情况：完成事务
        boolean isCompleted = handlerForCheckTransactionIsCompleted(tbTransactionActivity, tbTransactionActivityDetailList);
        if (isCompleted) {
            return;
        }
        //情况：回滚事务
    }

    /**
     * 检查当前事务是已经完成
     *
     * @param tbTransactionActivity
     * @param tbTransactionActivityDetailList
     * @return
     */
    private boolean handlerForCheckTransactionIsCompleted(TbTransactionActivity tbTransactionActivity, List<TbTransactionActivityDetail> tbTransactionActivityDetailList) {
        if (tbTransactionActivity.getTxcExecuteStatus() == ITransactionActivityEnum.ExecuteStatus.EXECUTE_EXCEPTION.getStatus()) {
            return false;
        }
        Long txcId=tbTransactionActivity.getTxcId();
        Integer txcActivityCode=tbTransactionActivity.getTxcActivityCode();
        ITransactionActivityEnum.Activities activityEnum=ITransactionActivityEnum.Activities.getEnum(txcActivityCode);
        if(activityEnum==null){
            log.error("全局事务ID={},事务活动代码={},但没有找到相对应的事务活动枚举",txcId,txcActivityCode);
        }
        SortedMap<Integer,String> stepMap=activityEnum.getStepMap();
        //判断流程是否发生中断，方法：通过两个字符串在起始对比是否包含即可。eg:1,2,3,4 与1,2
        //standardStep:标准流程;currentStep:当前流程
        Set<Integer>standardStep=stepMap.keySet();
        Set<Integer>currentStep=new TreeSet<>();
        tbTransactionActivityDetailList.forEach(item->currentStep.add(item.getTxcStepStatus()));
        String standardStepStr="";
        for(Integer item:standardStep){
            standardStepStr+=item.toString()+",";
        }
        String currentStepStr="";
        for(Integer item:currentStep){
            currentStepStr+=item.toString()+",";
        }

        return false;
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
