package com.example.demo.controller;

import com.example.demo.config.RabbitMQConfig2;
import com.example.demo.service.BusinessMessageSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        for (int i = 0; i < 100000; i++) {
            sender.sendAsyncMsg(msg, "test.im.routingkey.push.htoneMsg");
        }
        long endTime = System.currentTimeMillis();
        log.info("sendMsg finish!!,花费时间:{}", endTime - startTime);
        return ResponseEntity.ok("SUCCESS");
    }


    @PostMapping("fanout/{routingKey}")
    public ResponseEntity<String> fanout(@RequestBody String msg, @PathVariable("routingKey") String routingKey) {
        long startTime = System.currentTimeMillis();
        sender.sendAsyncMsg(msg, routingKey, RabbitMQConfig2.TEST_FANOUT_EXCHANGE);
        long endTime = System.currentTimeMillis();
        log.info("fanout finish!!,花费时间:{}", endTime - startTime);
        return ResponseEntity.ok("SUCCESS");
    }


    @PostMapping("topic/{routingKey}")
    public ResponseEntity<String> topic(@RequestBody String msg, @PathVariable("routingKey") String routingKey) {
        long startTime = System.currentTimeMillis();
        sender.sendAsyncMsg(msg, routingKey, RabbitMQConfig2.TEST_TOPIC_EXCHANGE);
        long endTime = System.currentTimeMillis();
        log.info("topic finish!!,花费时间:{}", endTime - startTime);
        return ResponseEntity.ok("SUCCESS");
    }


    @PostMapping("test/{routingKey}")
    public ResponseEntity<String> test(@RequestBody String msg, @PathVariable("routingKey") String routingKey) {
        long startTime = System.currentTimeMillis();
        log.info("test/{},msg:{}", routingKey, msg);
        long endTime = System.currentTimeMillis();
        log.info("topic finish!!,花费时间:{}", endTime - startTime);
        return ResponseEntity.ok("SUCCESS");
    }


}
