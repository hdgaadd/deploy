package org.codeman;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author hdgaadd
 * created on 2022/11/05
 */
@Service
public class InsideService {

    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private BloomFilterHelper<String> bloomFilterHelper;

    public void setConfigSwitch(String configKey) {
        int[] bitArr = bloomFilterHelper.murmurHashOffset(configKey);
        for (int bit : bitArr) {
            redisTemplate.opsForValue().setBit(configKey, bit, true);
        }
    }

    public boolean getConfigSwitch(String configKey) {
        int[] bitArr = bloomFilterHelper.murmurHashOffset(configKey);
        for (int bit : bitArr) {
            Boolean isBitExist = redisTemplate.opsForValue().getBit(configKey, bit);
            if (!isBitExist) {
                return false;
            }
        }
        return true;
    }
}
