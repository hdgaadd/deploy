package org.codeman.open;

import lombok.extern.slf4j.Slf4j;
import org.codeman.db.DBWrite;
import org.codeman.repository.Clock;
import org.redisson.Redisson;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author hdgaadd
 * Created on 2022/10/01
 */
@Component
@Slf4j
public class Consumer {

    @Resource
    private RedissonClient redissonClient;
    @Resource
    private DBWrite dbWrite;

    public void setClock(Integer delaySecond) throws IOException {
//        Config config = new Config();
//        config.useClusterServers().setScanInterval(2000)
//                .addNodeAddress("redis://106.14.172.7:7001", "redis://106.14.172.7:7002")
//                .addNodeAddress("redis://106.14.172.7:7003");
//        RedissonClient redissonClient = Redisson.create(config);


        RBlockingQueue<Clock> blockingFairQueue = redissonClient.getBlockingQueue("delay_queue");
        RDelayedQueue<Clock> delayedQueue = redissonClient.getDelayedQueue(blockingFairQueue);

        Clock clock = new Clock().setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        // write db
        dbWrite.write(clock);
        delayedQueue.offer(clock, delaySecond, TimeUnit.SECONDS);
        log.info("create clock: {}", clock);
    }
}
