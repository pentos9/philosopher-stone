package com.spacex.philosopher.controller;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class RateLimiterController {

    private Logger logger = LoggerFactory.getLogger(RateLimiterController.class);

    private static int RANDOM_STRING_LENGTH = 32;

    @GetMapping(value = "/random")
    public String random() {
        logger.info(String.format("RateLimiterController#random"));
        return RandomStringUtils.random(RANDOM_STRING_LENGTH, true, true);
    }
}
