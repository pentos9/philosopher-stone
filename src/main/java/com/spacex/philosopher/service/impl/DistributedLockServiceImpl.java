package com.spacex.philosopher.service.impl;

import com.spacex.philosopher.script.DistributedLockScript;
import com.spacex.philosopher.service.DistributedLockService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@Service
public class DistributedLockServiceImpl implements DistributedLockService {

    private static final Logger logger = LoggerFactory.getLogger(DistributedLockServiceImpl.class);

    private static final String LOCK_SUCCESS = "OK";

    private static final String SET_IF_NOT_EXIST = "NX";

    private static final String SET_WITH_EXPIRED_TIME = "PX";

    private static final Long RELEASE_SUCCESS = 1L;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean tryGetDistributedLock(final String lockKey, final String requestId, final Long expireTime) {
        logger.info(String.format("DistributedLockServiceImpl#tryGetDistributedLock: lockKey:%s,requestId:%s,expireTime:%s", lockKey, requestId, expireTime));
        boolean result =
                stringRedisTemplate.execute(new RedisCallback<Boolean>() {
                    @Override
                    public Boolean doInRedis(RedisConnection redisConnection) throws DataAccessException {

                        Object nativeConnection = redisConnection.getNativeConnection();
                        String redisStatusCode = "";

                        if (nativeConnection instanceof JedisCluster) {
                            JedisCluster jedisCluster = (JedisCluster) nativeConnection;
                            redisStatusCode = jedisCluster.set(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRED_TIME, expireTime);
                        } else if (nativeConnection instanceof Jedis) {
                            Jedis jedis = (Jedis) nativeConnection;
                            redisStatusCode = jedis.set(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRED_TIME, expireTime);
                        }

                        return StringUtils.equalsIgnoreCase(LOCK_SUCCESS, redisStatusCode);
                    }
                });


        return result;
    }

    @Override
    public boolean releaseDistributedLock(String lockKey, String requestId) {
        logger.info(String.format("DistributedLockServiceImpl#releaseDistributedLock lockKey:%s,requestId:%s", lockKey, requestId));
        String releaseDistributedLockScript = DistributedLockScript.getReleaseDistributedLockScript();
        RedisScript<Long> redisScript = new DefaultRedisScript<Long>(releaseDistributedLockScript, Long.TYPE);
        List<String> keys = Arrays.asList(lockKey);
        Long result = stringRedisTemplate.execute(redisScript, keys, requestId);
        return RELEASE_SUCCESS.equals(result);
    }
}
