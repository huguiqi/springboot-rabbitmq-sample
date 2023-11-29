package com.example.demo.service;

import com.example.demo.service.counter.TotalCounterFanout1;
import com.example.demo.service.counter.TotalCounterTopic1;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service("event4Receiver")
public class Event4ReceiverImpl extends BaseEventReceiver {


    @Override
    protected void processing(String body) {
        log.info("处理收到的event4数据,message:{}",body);
        TotalCounterTopic1 counter = TotalCounterTopic1.getInstance();
        counter.addCounter();
    }

    @Override
    protected void saveRetryFailMessage(String body) {
        log.info("{}.saveRetryFailMessage:{},保存重试失败的消息!!!", this.getClass().getName(), body);

    }
}
