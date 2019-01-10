package com.yzd.db.account.dao.utils.enum4ext;

/**
 * 本地事务确认日志表
 */
public interface ITxcMessageEnum {

    /**
     * 事务回滚状态确认
     */
    enum RollbackStatus implements ITxcMessageEnum {
        NO(0), YES(1);

        RollbackStatus(Integer status) {
            this.status = status;
        }

        private final Integer status;

        public Integer getStatus() {
            return status;
        }
    }
}
