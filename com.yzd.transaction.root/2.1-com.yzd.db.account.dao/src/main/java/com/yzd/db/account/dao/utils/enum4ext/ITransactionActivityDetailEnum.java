package com.yzd.db.account.dao.utils.enum4ext;

/**
 * 分支事务表的状态集合
 */
public interface ITransactionActivityDetailEnum {
    /**
     * 分支事务活动详情表的回滚状态
     */
    public enum RollbackStatusEnum{
        NO(0),YES(1);
        private final Integer value ;

        RollbackStatusEnum(Integer value){
            this.value=value;
        }

        public Integer getValue() {
            return value;
        }
    }
    public enum DatabaseNameEnum{
        USER_DB(0),ORDER_DB(1);
        private final Integer value ;

        DatabaseNameEnum(Integer value){
            this.value=value;
        }

        public Integer getValue() {
            return value;
        }
    }

}
