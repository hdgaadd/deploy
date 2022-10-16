package org.codeman;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author hdgaadd
 * created on 2022/10/14
 */
@Service
@Slf4j
public class InsideService {

    @Resource
    private RedissonClient redissonClient;
    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    private static final String CURRENT_LIMIT_KEY = "CURRENT_LIMIT_KEY";

    private static final int QPS = 1;

    public String pushMessage(String msg) {
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(CURRENT_LIMIT_KEY);
        // 针对推送接口最高QPS, 设置速率, 1秒中产生QPS个令牌
        rateLimiter.trySetRate(RateType.OVERALL, 1, QPS, RateIntervalUnit.SECONDS);
        // 获取令牌
        boolean isGet = rateLimiter.tryAcquire(1);

        if (isGet) {
            log.info("push message successful: " + msg);
        } else {
            kafkaTemplate.send("current-limit-topic", msg);
        }
        return "successful!";
    }
}
