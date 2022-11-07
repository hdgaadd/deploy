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

    private static final String CONFIG_KEY = "config_key";

    public void setConfigSwitch(String configVal) {
        int[] bitArr = bloomFilterHelper.murmurHashOffset(configVal);
        for (int bit : bitArr) {
            redisTemplate.opsForValue().setBit(CONFIG_KEY, bit, true);
        }
    }

    public boolean getConfigSwitch(String configVal) {
        int[] bitArr = bloomFilterHelper.murmurHashOffset(configVal);
        for (int bit : bitArr) {
            Boolean isBitExist = redisTemplate.opsForValue().getBit(CONFIG_KEY, bit);
            if (!isBitExist) {
                return false;
            }
        }
        return true;
    }
}
