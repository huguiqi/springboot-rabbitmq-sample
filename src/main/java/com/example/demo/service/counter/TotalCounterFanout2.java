package com.example.demo.service.counter;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.LongAccumulator;

public class TotalCounterFanout2 {

    private static final TotalCounterFanout2 TOTAL_COUNTER_1 = new TotalCounterFanout2();

    private TotalCounterFanout2() {
        super();
    }

    public static TotalCounterFanout2 getInstance() {
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
