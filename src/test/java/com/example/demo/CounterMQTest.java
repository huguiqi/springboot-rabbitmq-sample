package com.example.demo;

import com.example.demo.service.counter.TotalCounterTopic1;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class CounterMQTest {

    @Test
    public void testTotalResult() {
        TotalCounterTopic1 totalCounterTopic1 = TotalCounterTopic1.getInstance();
        List list = new ArrayList<Long>(100);
        for (Long i = 0L; i < 5000L; i++) {
            list.add(i);
        }
        list.parallelStream().forEach(a -> {
            totalCounterTopic1.addCounter();
        });

        Long currentValue = totalCounterTopic1.givenCurrentValue();
        System.out.println("结果：" + currentValue);

        Assert.assertEquals(5000L, currentValue.longValue());


        list.parallelStream().forEach(a -> {
            totalCounterTopic1.addCounter();
        });


        Assert.assertEquals(5000L, totalCounterTopic1.givenCurrentValue().longValue());
    }


    @Test
    public void testGivenCurrentCount() {
        TotalCounterTopic1 totalCounterTopic1 = TotalCounterTopic1.getInstance();
        Assert.assertEquals(0, totalCounterTopic1.givenCurrentValue().longValue());
    }

    @Test
    public void testLongMaxSize() {
        System.out.println(Long.MAX_VALUE);
    }
}
