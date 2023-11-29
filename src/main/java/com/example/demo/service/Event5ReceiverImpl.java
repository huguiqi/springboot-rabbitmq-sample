package com.example.demo.service;

import com.example.demo.service.counter.TotalCounterTopic1;
import com.example.demo.service.counter.TotalCounterTopic2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service("event5Receiver")
public class Event5ReceiverImpl extends BaseEventReceiver {


    @Override
    protected void processing(String body) {
        log.info("处理收到的event5数据,message:{}",body);
        TotalCounterTopic2 counter = TotalCounterTopic2.getInstance();
        counter.addCounter();
    }

    @Override
    protected void saveRetryFailMessage(String body) {
        log.info("{}.saveRetryFailMessage:{},保存重试失败的消息!!!", this.getClass().getName(), body);

    }
}
