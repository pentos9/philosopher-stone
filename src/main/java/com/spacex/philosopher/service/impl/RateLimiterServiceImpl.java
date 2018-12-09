package com.spacex.philosopher.service.impl;

import com.spacex.philosopher.script.RateLimiterLuaScript;
import com.spacex.philosopher.service.RateLimiterService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@Service
public class RateLimiterServiceImpl implements RateLimiterService {

    private Logger logger = LoggerFactory.getLogger(RateLimiterServiceImpl.class);

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean acquire(String key, Long limitMax, Long expiredTime) {
        logger.info(String.format("RateLimiterServiceImpl#acquire key:%s,limitMax:%s,expiredTime:%s", key, limitMax, expiredTime));
        List<String> keys = Arrays.asList(key);
        String rateLimiterLuaScript = RateLimiterLuaScript.getRateLimiterScript();
        RedisScript<Boolean> luaScript = new DefaultRedisScript<Boolean>(rateLimiterLuaScript, Boolean.class);
        boolean result = stringRedisTemplate.execute(luaScript, keys, limitMax.toString(), expiredTime.toString());
        return result;
    }
}
