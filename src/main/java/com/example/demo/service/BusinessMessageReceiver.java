package com.example.demo.service;

import com.alibaba.fastjson.JSON;
import com.example.demo.bean.IMMQMessage;
import com.example.demo.config.RabbitMQConfig;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Slf4j
@Component
public class BusinessMessageReceiver {

    @RabbitListener(queues = RabbitMQConfig.IM_PUSH_QUEUE_NAME)
    public void receiveA(Message message, Channel channel) throws IOException {
        IMMQMessage immqMessage = null;
        String msg = null;
        boolean ack = true;
        Exception exception = null;

        try {
            String body = new String(message.getBody(),"utf-8");
            immqMessage = JSON.parseObject(body, IMMQMessage.class);
            msg = immqMessage.getBody();
            log.info("收到业务消息A：{}", msg);
        } catch (Exception e) {
            ack = false;
            exception = e;
        }

        if (!ack) {
            log.error("消息格式不正确，error msg:{}", message.getBody());
            //ack返回false，并重新回到当前队列(这种情况会发生消息堵塞)
//            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false,true);
            //ack返回true，告诉服务器收到这条消息 已经被我消费了 可以在队列删掉 这样以后就不会再发了
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            return;
        }

        if (immqMessage.getRetrySize() < 3) {
            try {
                if (msg.contains("deadletter")) {
                    throw new RuntimeException("dead letter exception");
                }
            } catch (Exception e) {
                ack = false;
                exception = e;
            }
        }

        if (!ack) {
            log.error("消息消费发生异常，error msg:{}", exception.getMessage(), exception);
            //ack返回false，并将此条消息从队列中丢弃,因为默认交换机配置了xdl（死信）,则被丢弃的消息会跑到死信队列里
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        } else {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }
    }

//    @RabbitListener(queues = BUSINESS_QUEUEB_NAME)
//    public void receiveB(Message message, Channel channel) throws IOException {
//        System.out.println("收到业务消息B：" + new String(message.getBody()));
//        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
//    }
}
