package com.yzd.db.account.dao.utils.enum4ext;

public class PublicEnum {

    public enum GmtIsDeletedEnum{
        NO(0),YED(1);
        private final Integer value ;

        GmtIsDeletedEnum(Integer value){
            this.value=value;
        }

        public Integer getValue() {
            return value;
        }
    }

    /**
     * 分支事务活动详情表的回滚状态
     */
    public enum TxcActivityDetailRollbackStatusEnum{
        NO(0),YED(1);
        private final Integer value ;

        TxcActivityDetailRollbackStatusEnum(Integer value){
            this.value=value;
        }

        public Integer getValue() {
            return value;
        }
    }
}
