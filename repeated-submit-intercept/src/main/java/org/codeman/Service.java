package org.codeman;

import org.codeman.component.BanRepeatSubmit;

/**
 * @author hdgaadd
 * created on 2022/10/30
 */
@org.springframework.stereotype.Service
public class Service {

    @BanRepeatSubmit(lockTime = 6)
    public String businessMethod() throws InterruptedException {
        // 线程睡眠，模拟业务执行
        Thread.sleep(3000);
        return "successful!";
    }
}
