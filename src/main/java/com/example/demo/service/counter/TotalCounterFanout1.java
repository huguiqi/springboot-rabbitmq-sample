package com.example.demo.service.counter;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.LongAccumulator;

public class TotalCounterFanout1 {

    private static final TotalCounterFanout1 TOTAL_COUNTER_1 = new TotalCounterFanout1();

    private TotalCounterFanout1(){
        super();
    }

    public static TotalCounterFanout1 getInstance(){
        return TOTAL_COUNTER_1;
    }

    //初始化数据
    private static  LongAccumulator count = new LongAccumulator((x, y) -> x + y, 0L);

    public  void incr() {
        count.accumulate(1);
    }

    public synchronized void addCounter(){
            incr();
    }


   public Long givenCurrentValue(){
        return count.getThenReset();
   }

}
