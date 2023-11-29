package com.example.demo.service;

import com.example.demo.service.counter.TotalCounterFanout2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.stereotype.Service;

import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service("event3Receiver")
public class Event3ReceiverImpl extends BaseEventReceiver {

//    private ReentrantLock lock = new ReentrantLock(true);


    @Override
    protected void processing(String body) {
        log.info("处理收到的event3数据,message:{}", body);
//        lock.tryLock();
        TotalCounterFanout2 counter = TotalCounterFanout2.getInstance();
        counter.addCounter();
//        lock.unlock();
    }

    @Override
    protected void saveRetryFailMessage(String body) {
        log.info("{}.saveRetryFailMessage:{},保存重试失败的消息!!!", this.getClass().getName(), body);

    }
}
