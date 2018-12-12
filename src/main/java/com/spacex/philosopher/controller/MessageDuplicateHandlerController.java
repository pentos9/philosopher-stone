package com.spacex.philosopher.controller;

import com.spacex.philosopher.dto.MessageBodyDTO;
import com.spacex.philosopher.dto.SimpleBooleanResultDTO;
import com.spacex.philosopher.service.DistributedLockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.UUID;

@RestController
public class MessageDuplicateHandlerController {

    private Logger logger = LoggerFactory.getLogger(MessageDuplicateHandlerController.class);

    @Resource
    private DistributedLockService distributedLockService;

    @RequestMapping(value = "message/duplicate/handler", method = RequestMethod.POST)
    public SimpleBooleanResultDTO messageDuplicateHandler(@RequestBody MessageBodyDTO messageBodyDTO) {

        String uuid = UUID.randomUUID().toString();
        messageBodyDTO.setId(uuid);//mock biz unique id
        String key = "message:duplicate:handler:block:" + ":" + uuid;

        String content = messageBodyDTO.getContent();
        boolean isOK = distributedLockService.tryGetDistributedLock(key, content, 2000L);
        if (!isOK) {
            return new SimpleBooleanResultDTO(false);
        }

        doJob();

        return new SimpleBooleanResultDTO(true);
    }

    private void doJob() {
        logger.info(String.format("MessageDuplicateHandlerController#messageDuplicateHandler do job now"));
    }

}
