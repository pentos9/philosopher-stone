package com.spacex.philosopher.controller;

import com.spacex.philosopher.dto.SimpleBooleanResultDTO;
import com.spacex.philosopher.service.DistributedLockService;
import org.assertj.core.util.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class DistributedLockController {

    private Logger logger = LoggerFactory.getLogger(DistributedLockController.class);

    @Resource
    private DistributedLockService distributedLockService;

    @GetMapping("redis/distributed/lock")
    public SimpleBooleanResultDTO distributedLock() {
        concurrent();
        return new SimpleBooleanResultDTO(true);
    }

    public void concurrent() {
        List<Boolean> results = Lists.newArrayList();

        CountDownLatch startLatch = new CountDownLatch(1);

        final int currentSize = 50;
        CountDownLatch bizLatch = new CountDownLatch(currentSize);

        ExecutorService executorService = Executors.newFixedThreadPool(currentSize);

        for (int i = 0; i < currentSize; i++) {
            executorService.execute(() -> {
                try {
                    startLatch.await();

                    boolean result = businessLogic();
                    if (result) {
                        results.add(result);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    bizLatch.countDown();
                }
            });
        }

        startLatch.countDown();

        try {
            bizLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        executorService.shutdown();

        System.out.println("[Task] Done");
        System.out.println(results);

    }

    public boolean businessLogic() {
        String key = "redis:distributed:lock:test";
        String requestId = UUID.randomUUID().toString();
        Long taskToBeDoneTime = 3000L;
        try {
            boolean isOK = distributedLockService.tryGetDistributedLock(key, requestId, taskToBeDoneTime);
            if (!isOK) {
                return false;
            }

            doJob(taskToBeDoneTime);
            return true;

        } finally {
            distributedLockService.releaseDistributedLock(key, requestId);
        }
    }


    public void doJob(Long taskToBeDoneTime) {
        logger.info("do job...");
        try {
            Thread.sleep(taskToBeDoneTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
