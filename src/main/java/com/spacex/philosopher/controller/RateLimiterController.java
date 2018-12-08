package com.spacex.philosopher.controller;

import com.spacex.philosopher.dto.SimpleBooleanResultDTO;
import com.spacex.philosopher.service.RateLimiterService;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@RestController
public class RateLimiterController {

    private Logger logger = LoggerFactory.getLogger(RateLimiterController.class);

    private static final int RANDOM_STRING_LENGTH = 32;

    @Resource
    private RateLimiterService rateLimiterService;

    @GetMapping(value = "/random")
    public String random() {
        logger.info(String.format("RateLimiterController#random"));
        return RandomStringUtils.random(RANDOM_STRING_LENGTH, true, true);
    }

    @GetMapping(value = "/rate/limiter")
    public SimpleBooleanResultDTO acquire() {
        String key = "rate:limit:test:";
        boolean isOK = rateLimiterService.acquire(key, 9L, 60L);
        logger.info(String.format("isOK:%s", isOK));
        SimpleBooleanResultDTO simpleBooleanResultDTO = new SimpleBooleanResultDTO(isOK);
        return simpleBooleanResultDTO;
    }
}
