package com.yzd.db.account.dao.utils.enum4ext;

public class PublicEnum {

    public enum GmtIsDeletedEnum{
        NO(0),YES(1);
        private final Integer value ;

        GmtIsDeletedEnum(Integer value){
            this.value=value;
        }

        public Integer getValue() {
            return value;
        }
    }
}
