package com.example.demo.service;

import com.example.demo.service.counter.TotalCounterFanout1;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service("event2Receiver")
public class Event2ReceiverImpl extends BaseEventReceiver {

//    @Autowired
//    private TotalCounterFanout1 counter;

    @Override
    protected void processing(String body) {
        log.info("处理收到的event2数据,message:{}", body);
        TotalCounterFanout1 counter = TotalCounterFanout1.getInstance();
        counter.addCounter();
    }


    public void processing2(String body) {
        log.info("处理收到的event2数据,message:{}", body);
        TotalCounterFanout1 counter = TotalCounterFanout1.getInstance();
        counter.addCounter();
    }

    @Override
    protected void saveRetryFailMessage(String body) {
        log.info("{}.saveRetryFailMessage:{},保存重试失败的消息!!!", this.getClass().getName(), body);

    }
}
