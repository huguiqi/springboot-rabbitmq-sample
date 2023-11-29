package com.example.demo.service.counter;

import java.util.concurrent.atomic.LongAccumulator;

public class TotalCounterSendMQ {

    private static final TotalCounterSendMQ TOTAL_COUNTER_1 = new TotalCounterSendMQ();

    private TotalCounterSendMQ() {
        super();
    }

    public static TotalCounterSendMQ getInstance() {
        return TOTAL_COUNTER_1;
    }

    //初始化数据
    private static LongAccumulator count = new LongAccumulator((x, y) -> x + y, 0L);

    public synchronized void incr() {
        count.accumulate(1);
    }

    public void addCounter() {
        incr();
    }


    public Long givenCurrentValue() {
        return count.getThenReset();
    }

}
