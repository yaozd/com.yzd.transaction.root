package com.yzd.db.account.dao.utils.enum4ext;

/**
 * 活动交易流程状态
 */
public interface ITransactionActivityDetailStatusEnum {
    Integer getStatus();
    String getDescribe();

    /**
     * 用户转账交易流程-状态列表
     */
    enum TransferMoney implements ITransactionActivityDetailStatusEnum{
        TRANSFER(1,"转账"),ENTER(2,"入账");
        private TransferMoney(Integer status, String describe) {
            this.status = status;
            this.describe = describe;
        }

        private final String describe;
        private final Integer status;

        public Integer getStatus() {
            return this.status;
        }

        public String getDescribe() {
            return this.describe;
        }
    }
}
