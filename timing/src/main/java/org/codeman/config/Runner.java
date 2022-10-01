package org.codeman.config;

import lombok.extern.slf4j.Slf4j;
import org.codeman.repository.Clock;
import org.redisson.Redisson;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author hdgaadd
 * Created on 2022/10/01
 */
@Component
@Slf4j
public class Runner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://106.14.172.7:6379");
        RedissonClient redissonClient = Redisson.create(config);
        RBlockingQueue<Clock> blockingFairQueue = redissonClient.getBlockingQueue("delay_queue");

        while (true) {
            Clock callCdr = blockingFairQueue.take();
            log.info("time out: {} , clock created: {}", new SimpleDateFormat("HH:mm:ss").format(new Date()), callCdr.getTime());
        }
    }
}
