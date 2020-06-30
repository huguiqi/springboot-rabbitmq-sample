package com.example.demo.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.bean.IMMQMessage;
import com.example.demo.config.RabbitMQConfig;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.example.demo.config.RabbitMQConfig.IM_DELAY_ROUTING_KEY;


@Slf4j
@Component
public class DeadLetterMessageReceiver {

    @Autowired
    private BusinessMessageSender businessMessageSender;

    /**
     * 转发设置延时重试设置
     *
     * @param message
     * @param channel
     * @throws IOException
     */
    @RabbitListener(queues = RabbitMQConfig.IM_DEAD_LETTER_QUEUE_NAME)
    public void receiveA(Message message, Channel channel) throws IOException {
        String body = new String(message.getBody());
        log.info("收到死信消息A：{}", body);
        log.info("死信消息properties：{}", message.getMessageProperties());
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        //作为生产者再次将消息放入
        IMMQMessage immqMessage = JSON.parseObject(body, IMMQMessage.class);
        immqMessage.setRetrySize(immqMessage.getRetrySize() + 1);
        businessMessageSender.sendMsg(immqMessage, IM_DELAY_ROUTING_KEY);
    }

//    @RabbitListener(queues = DEAD_LETTER_QUEUEB_NAME)
//    public void receiveB(Message message, Channel channel) throws IOException {
//        System.out.println("收到死信消息B：" + new String(message.getBody()));
//        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
//    }
}
