package com.yzd.db.account.dao.service.inf;

import com.yzd.db.account.entity.table.TbAccount;
import com.yzd.db.account.entity.table.TbTxcMessage;
import com.yzd.db.account.entity.tableExt.TbAccountExt.TbAccount4Payment;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface IAccountInf {
    /**
     * @param id
     * @return
     */
    TbAccount selectById(Long id);

    /**
     * 扣除用户余额
     * 1.不推荐使用大事务，通过REQUIRES_NEW来缩小事务的范围
     *
     * @param item
     * @return
     */
    //@Transactional(propagation = Propagation.REQUIRES_NEW)
    int payment(TbAccount4Payment item);

    /**
     * 转账-通过分布式事务
     *
     * @param item
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    int paymentByTransactional(TbAccount4Payment item);

    /**
     *
     *
     * @param whereTxcMessage
     * @param item
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    boolean paymentForRollbackByTransactional(TbTxcMessage whereTxcMessage, TbAccount4Payment item);
}
