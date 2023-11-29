package com.example.demo.service;

import com.alibaba.fastjson.JSON;
import com.example.demo.bean.BizMQMessage;
import com.example.demo.config.RabbitMQConfig2;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;



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
    @RabbitListener(queues = RabbitMQConfig2.BIZ_DEAD_LETTER_PUSH_QUEUE_NAME)
    public void receivePush(Message message, Channel channel) throws IOException {
        String body = "";
        try {
            body = new String(message.getBody(), "utf-8");
            log.info("收到公众号消息推送的死信消息：{}", body);
            log.info("公众号消息推送的死信消息properties：{}", message.getMessageProperties());
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            //作为生产者再次将消息放入
            BizMQMessage bizMQMessage = JSON.parseObject(body, BizMQMessage.class);
            bizMQMessage.setRetrySize(bizMQMessage.getRetrySize() + 1);
            businessMessageSender.sendMsgWithDelay(bizMQMessage, RabbitMQConfig2.BIZ_DELAY_ROUTING_KEY,60l);
        } catch (Exception e) {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            log.error("接收死信消息时发生错误，消息将会丢失!!!,msg:{}", body, e);
        }
    }








}
