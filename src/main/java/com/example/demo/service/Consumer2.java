package com.example.demo.service;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

/**
 * Created by sam on 2018/3/27.
 */
@Component
public class Consumer2 {

    @JmsListener(destination = "mytest.queue")
    @SendTo("out.queue")
    public String receiveQueue(String text) {
        System.out.println("消费者2收到:"+text);
        return "儿子收到"+text;
    }
}
