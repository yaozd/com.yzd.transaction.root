package com.yzd.db.account.dao.utils.enum4ext;

/**
 * 分支事务表的活动交易流程状态
 */
public interface ITransactionActivityDetail4StepStatusEnum {
    Integer getStatus();

    String getName();

    /**
     * 用户转账交易流程-状态列表
     */
    enum TransferMoney implements ITransactionActivityDetail4StepStatusEnum {
        TRANSFER(1, "转账"), ENTER(2, "入账");

        private TransferMoney(Integer status, String name) {
            this.status = status;
            this.name = name;
        }

        private final String name;
        private final Integer status;

        public Integer getStatus() {
            return this.status;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
