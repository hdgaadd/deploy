package org.codeman;

import org.redisson.Redisson;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.concurrent.TimeUnit;

/**
 * @author hdgaadd
 * Created on 2022/09/28
 *
 * 创建10个延迟1min的订单请求，1min后发送订单消息
 */
public class RedisPutInQueue {
    public static void main(String args[]) {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        RedissonClient redissonClient = Redisson.create(config);
        RBlockingQueue<Employer> blockingFairQueue = redissonClient.getBlockingQueue("delay_queue");
        RDelayedQueue<Employer> delayedQueue = redissonClient.getDelayedQueue(blockingFairQueue);

        for (int i = 0; i < 10; i++) {
            try {
                //模拟间隔投递消息
                Thread.sleep(1 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Employer callCdr = new Employer();
            callCdr.setSalary(345.6);
            callCdr.setPutTime();
            delayedQueue.offer(callCdr, 1, TimeUnit.MINUTES);
            System.out.println("callCdr =================================> " + callCdr);
        }

        //在该对象不再需要的情况下，应该主动销毁。
        // 仅在相关的Redisson对象也需要关闭的时候可以不用主动销毁。
        delayedQueue.destroy();
        //redissonClient.shutdown();
    }
}
