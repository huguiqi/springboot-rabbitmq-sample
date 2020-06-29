package com.example.demo.controller;

import com.example.demo.bean.IMMQMessage;
import com.example.demo.service.BusinessMessageSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("rabbitmq")
@RestController
public class RabbitMQMsgController {

    @Autowired
    private BusinessMessageSender sender;

    @RequestMapping("sendmsg")
    public ResponseEntity<String> sendMsg(String msg) {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            sender.sendMsg(msg, "test.im.routingkey.push.htoneMsg");
        }
        long endTime = System.currentTimeMillis();
        log.info("sendMsg finish!!,花费时间:{}", endTime - startTime);
        return ResponseEntity.ok("SUCCESS");
    }

    @RequestMapping("asyncSendmsg")
    public ResponseEntity<String> asyncSendMsg(@RequestBody String msg) {
        long startTime = System.currentTimeMillis();
//        for (int i = 0; i < 1000; i++) {
            sender.sendAsyncMsg(msg, "test.im.routingkey.push.htoneMsg");
//        }
        long endTime = System.currentTimeMillis();
        log.info("sendMsg finish!!,花费时间:{}", endTime - startTime);
        return ResponseEntity.ok("SUCCESS");
    }
}
