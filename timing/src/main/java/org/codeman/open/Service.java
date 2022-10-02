package org.codeman.open;

import lombok.extern.slf4j.Slf4j;
import org.codeman.repository.Clock;
import org.redisson.Redisson;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author hdgaadd
 * Created on 2022/10/01
 */
@Component
@Slf4j
public class Service {
    public void setClock(Integer delaySecond) {
        Config config = new Config();
        config.useClusterServers()
                .setScanInterval(2000) // cluster state scan interval in milliseconds
                // use "rediss://" for SSL connection
//                .addNodeAddress("redis://106.14.172.7:7001", "redis://106.14.172.7:7002",  "redis://106.14.172.7:7003", "redis://106.14.172.7:7004", "redis://106.14.172.7:7005", "redis://106.14.172.7:7006");
                .addNodeAddress("redis://106.14.172.7:7001", "redis://106.14.172.7:7002").addNodeAddress("redis://106.14.172.7:7003");

//        RedissonClient redisson = Redisson.create(config);

//        Config config = new Config();
//        config.useSingleServer().setAddress("redis://106.14.172.7:6379");
        RedissonClient redissonClient = Redisson.create(config);
        RBlockingQueue<Clock> blockingFairQueue = redissonClient.getBlockingQueue("delay_queue");
        RDelayedQueue<Clock> delayedQueue = redissonClient.getDelayedQueue(blockingFairQueue);


        Clock callCdr = new Clock().setTime(new SimpleDateFormat("HH:mm:ss").format(new Date()));
        delayedQueue.offer(callCdr, delaySecond, TimeUnit.SECONDS);
        log.info("create clock: {}", callCdr);
    }
}
