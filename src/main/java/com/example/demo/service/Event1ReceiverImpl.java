package com.example.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service("event1Receiver")
public class Event1ReceiverImpl extends BaseEventReceiver {


    @Override
    protected void processing(String body) {
        log.info("处理收到的event1数据,message:{}",body);
    }

    @Override
    protected void saveRetryFailMessage(String body) {
        log.info("{}.saveRetryFailMessage:{},保存重试失败的消息!!!", this.getClass().getName(), body);

    }
}
