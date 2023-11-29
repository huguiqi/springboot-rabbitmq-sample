package com.example.demo.service.counter;

import java.util.concurrent.atomic.LongAccumulator;

public class TotalCounterTopic2 {

    private static final TotalCounterTopic2 TOTAL_COUNTER_1 = new TotalCounterTopic2();

    private TotalCounterTopic2(){
        super();
    }

    public static TotalCounterTopic2 getInstance(){
        return TOTAL_COUNTER_1;
    }

    //初始化数据
    private static LongAccumulator count = new LongAccumulator((x, y) -> x + y, 0L);

    public static void incr() {
        count.accumulate(1);
    }

    public synchronized void addCounter(){
            incr();
    }


   public Long givenCurrentValue(){
        return count.getThenReset();
   }

}
