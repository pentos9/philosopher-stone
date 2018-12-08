package com.spacex.philosopher.service;

public interface RateLimiterService {
    boolean acquire(String key, Long limitMax, Long expiredTime);
}
