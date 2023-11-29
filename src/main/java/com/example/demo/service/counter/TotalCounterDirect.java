package com.example.demo.service.counter;

import java.util.concurrent.atomic.LongAccumulator;

public class TotalCounterDirect {

    private static final TotalCounterDirect TOTAL_COUNTER_1 = new TotalCounterDirect();

    private TotalCounterDirect(){
        super();
    }

    public static TotalCounterDirect getInstance(){
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
