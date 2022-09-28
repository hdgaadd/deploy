package org.codeman;

import org.redisson.Redisson;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author hdgaadd
 * Created on 2022/09/28
 *
 * 剔除所有订单消息
 */
public class RedisOutFromQueue {
    public static void main(String args[]) {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        RedissonClient redissonClient = Redisson.create(config);
        RBlockingQueue<Employer> blockingFairQueue = redissonClient.getBlockingQueue("delay_queue");

        while (true) {
            Employer callCdr = null;
            try {
                callCdr = blockingFairQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("订单取消时间：" + new SimpleDateFormat("hh:mm:ss").format(new Date()) + "==订单生成时间" + callCdr.getPutTime());
        }
    }
}

