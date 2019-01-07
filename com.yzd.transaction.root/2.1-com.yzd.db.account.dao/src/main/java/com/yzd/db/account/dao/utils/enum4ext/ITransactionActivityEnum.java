package com.yzd.db.account.dao.utils.enum4ext;

/***
 * 主事务记录表的状态集合
 */
public interface ITransactionActivityEnum {

    /**
     * 主事务-调度结果
     */
    enum TriggerStatus implements ITransactionActivityEnum {
        RUNNING(1, "进行中"), COMPETE(2, "完成");

        //region
        //---------------------------------------------------
        private TriggerStatus(Integer status, String describe) {
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
        //---------------------------------------------------
        //endregion
    }

    /**
     * 主事务-执行结果
     */
    enum ExecuteStatus implements ITransactionActivityEnum {
        EXECUTE_SUCCESS(1,"执行成功"),EXECUTE_EXCEPTION(2,"执行异常"),ROLLBACK_SUCCESS(3,"回滚成功");
        //region
        //---------------------------------------------------
        private ExecuteStatus(Integer status, String describe) {
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
        //---------------------------------------------------
        //endregion
    }
}
