package com.example.demo.service.consumer;

import com.example.demo.config.RabbitMQConfig2;
import com.example.demo.service.BaseEventReceiver;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class BusinessAConsumer {

    @Autowired
    private BaseEventReceiver event1Receiver;


    @Autowired
    private BaseEventReceiver event2Receiver;

    @Autowired
    private BaseEventReceiver event3Receiver;

    @Autowired
    private BaseEventReceiver event4Receiver;

    @Autowired
    private BaseEventReceiver event5Receiver;

    //rbmq 投递
    @RabbitListener(queues = RabbitMQConfig2.EVENT1_QUEUE_NAME)
    public void pushReceiver(Message message, Channel channel) throws IOException {

        event1Receiver.receive(message,channel);
    }


    //rbmq 订阅1(fanout)
    @RabbitListener(queues = RabbitMQConfig2.EVENT2_QUEUE_NAME)
    public void event2Receiver(Message message, Channel channel) throws IOException {

        event2Receiver.receive(message,channel);
    }


    //rbmq 订阅2(fanout)
    @RabbitListener(queues = RabbitMQConfig2.EVENT3_QUEUE_NAME)
    public void event3Receiver(Message message, Channel channel) throws IOException {

        event3Receiver.receive(message,channel);
    }



    //rbmq topic1
    @RabbitListener(queues = RabbitMQConfig2.EVENT4_QUEUE_NAME)
    public void event4Receiver(Message message, Channel channel) throws IOException {

        event4Receiver.receive(message,channel);
    }


    //rbmq topic2
    @RabbitListener(queues = RabbitMQConfig2.EVENT5_QUEUE_NAME)
    public void event5Receiver(Message message, Channel channel) throws IOException {

        event5Receiver.receive(message,channel);
    }

}
