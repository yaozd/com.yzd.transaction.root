package com.yzd.db.account.dao.utils.mock4ext;

import lombok.extern.slf4j.Slf4j;

/**
 * 模拟
 */
@Slf4j
public class MockUtil {
    private MockUtil() {
        throw new IllegalStateException("Utility class");
    }
    /**
     * 模拟异常
     */
    public static void mockException() {
        if (System.currentTimeMillis() > 100000000L) {
            throw new IllegalStateException("模拟异常");
        }
    }

    /**
     * 模拟异常
     * eg:
     * if(System.currentTimeMillis()>100000000L) throw new IllegalStateException("模拟异常");
     */
    public static void mockExceptionByTime() {
         if (System.currentTimeMillis() > 100000000L){
            throw new IllegalStateException("模拟异常");
        }
    }

    /**
     * 模拟休眠
     * @param millis
     */
    public static void mockSleep(Long millis){
        if (System.currentTimeMillis() > 100000000L){
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                log.warn("Interrupted!", e);
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * 休眠-然后再抛出异常
     * @param millis
     */
    public static void mockSleepAndThrowException(Long millis){
        if (System.currentTimeMillis() > 100000000L) {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                log.warn("Interrupted!", e);
                Thread.currentThread().interrupt();
            }
            throw new IllegalStateException("模拟异常,休眠（millis）：" + millis);
        }
    }
}
