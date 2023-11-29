package com.example.demo.controller;

import com.example.demo.config.RabbitMQConfig2;
import com.example.demo.service.BusinessMessageSender;
import com.example.demo.service.Event2ReceiverImpl;
import com.example.demo.service.counter.TotalCounterFanout1;
import com.example.demo.service.counter.TotalCounterSendMQ;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequestMapping("rabbitmq")
@RestController
public class RabbitMQMsgController {

    @Autowired
    private BusinessMessageSender sender;

    @Autowired
    private Event2ReceiverImpl event2Receiver;

    private boolean flag = true;

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


    @PostMapping("fanout/{nums}")
    public ResponseEntity<String> fanout(@RequestBody String msg, @PathVariable("nums") Long nums) {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < nums; i++) {
            sender.sendAsyncMsg(msg, null, RabbitMQConfig2.TEST_FANOUT_EXCHANGE);
        }

        long endTime = System.currentTimeMillis();
        log.info("fanout finish!!,花费时间:{}", endTime - startTime);
        return ResponseEntity.ok("SUCCESS");
    }


    @PostMapping("fanout")
    public ResponseEntity<String> fanout2(@RequestBody String msg) {
        sender.sendAsyncMsg(msg, null, RabbitMQConfig2.TEST_FANOUT_EXCHANGE);
        return ResponseEntity.ok("SUCCESS");
    }


    @GetMapping("/settingFlag/{flag}")
    public ResponseEntity<String> settingFlag(@PathVariable("flag") Boolean inputFlag) {
        flag = inputFlag;
        return ResponseEntity.ok("SUCCESS,flag:" + flag);
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


    @GetMapping("testCount/{size}")
    public ResponseEntity<String> testCount(@PathVariable("size") Long size) {
        long startTime = System.currentTimeMillis();
        long endTime = System.currentTimeMillis();
        List<Long> list = new ArrayList<>(100);
        for (Long i = 0L; i < size; i++) {
            list.add(i);
        }
        list.parallelStream().forEach(aLong -> {
            System.out.println(aLong);
            event2Receiver.processing2("helloaaaa");
        });

        log.info("topic finish!!,花费时间:{}", endTime - startTime);
        return ResponseEntity.ok("SUCCESS");
    }


    @GetMapping("getCount")
    public ResponseEntity<String> getCount() {
        long startTime = System.currentTimeMillis();
        long endTime = System.currentTimeMillis();
        TotalCounterFanout1 counterFanout1 = TotalCounterFanout1.getInstance();

        log.info("topic finish!!,花费时间:{}", endTime - startTime);
        return ResponseEntity.ok("success:" + counterFanout1.givenCurrentValue());
    }
}
