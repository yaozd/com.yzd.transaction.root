package com.yzd.db.account.dao.utils.enum4ext;

/***
 * 主事务记录表的状态集合
 */
public interface ITransactionActivityEnum {

    /**
     * 事务活动代码与名称
     * 每个活动对应一个交易流程状态
     */
    enum Activities implements ITransactionActivityEnum {
        TRANSFER_MONEY(1, "转账交易");

        //region
        //---------------------------------------------------
        private Activities(Integer code, String name) {
            this.code = code;
            this.name = name;
        }

        private final String name;
        private final Integer code;

        /**
         * 事务活动代码
         *
         * @return
         */
        public Integer getCode() {
            return code;
        }

        /**
         * 事务活动名称
         *
         * @return
         */
        public String getName() {
            return name;
        }

        //---------------------------------------------------
        //endregion
    }

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
     * PS:
     * INVALID_TRANSACTION(无效的事务):指事务只是做了初始化，并没有实际运行。
     */
    enum ExecuteStatus implements ITransactionActivityEnum {
        EXECUTE_RUNNING(1, "正在执行"), EXECUTE_EXCEPTION(2, "执行异常"), EXECUTE_SUCCESS(3, "执行成功"), ROLLBACK_SUCCESS(4, "回滚成功"), INVALID_TRANSACTION(999, "无效的事务");

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
