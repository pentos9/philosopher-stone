package com.spacex.philosopher.service;

public interface DistributedLockService {
    boolean tryGetDistributedLock(String lockKey, String requestId, Long expireTime);

    boolean releaseDistributedLock(String lockKey, String requestId);
}
