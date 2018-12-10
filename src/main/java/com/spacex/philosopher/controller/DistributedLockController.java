package com.spacex.philosopher.controller;

import com.spacex.philosopher.dto.SimpleBooleanResultDTO;
import com.spacex.philosopher.service.DistributedLockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.UUID;

@RestController
public class DistributedLockController {

    private Logger logger = LoggerFactory.getLogger(DistributedLockController.class);

    @Resource
    private DistributedLockService distributedLockService;

    @GetMapping("redis/distributed/lock")
    public SimpleBooleanResultDTO distributedLock() {

        String key = "redis:distributed:lock:test";
        String requestId = UUID.randomUUID().toString();
        try {
            boolean isOK = distributedLockService.tryGetDistributedLock(key, requestId, 5L);
            if (!isOK) {
                SimpleBooleanResultDTO simpleBooleanResultDTO = new SimpleBooleanResultDTO(false);
                return simpleBooleanResultDTO;
            }

            doJob();

        } finally {
            distributedLockService.releaseDistributedLock(key, requestId);
        }

        return new SimpleBooleanResultDTO(true);
    }


    public void doJob() {
        logger.info("do job...");
    }
}
