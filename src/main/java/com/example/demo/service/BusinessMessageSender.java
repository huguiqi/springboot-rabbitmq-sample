package com.example.demo.service;

import com.alibaba.fastjson.JSON;
import com.example.demo.bean.IMMQMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Address;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.annotation.PostConstruct;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import static com.example.demo.config.RabbitMQConfig.*;

@Slf4j
@Component
public class BusinessMessageSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private AsyncRabbitTemplate asyncRabbitTemplate;


    @PostConstruct
    public void init() {
        rabbitTemplate.setConfirmCallback((correlationData, ack, error) -> {
            log.info("消息唯一标识：" + correlationData);
            log.info("确认结果：" + ack);
            if (ack) {
                log.info("消息确认发送成功!!,msgId:{}", correlationData);
            } else {
                log.warn("失败原因：{}", error);
            }
        });


        //成功消费不会回调,没有正确路由到合适的队列，就会回调
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String body = null;
            try {
                body = new String(message.getBody(), "utf-8");
                log.info("return--message:" + body + ",replyCode:" + replyCode
                        + ",replyText:" + replyText + ",exchange:" + exchange + ",routingKey:" + routingKey);
            } catch (UnsupportedEncodingException e) {
                log.error("mq返回的数据解析错误,e:", e);
            }
        });
    }

    public void sendMsg(String msg, String routingKey) {
        IMMQMessage message = IMMQMessage.builder().body(msg).build();
        rabbitTemplate.convertSendAndReceive(IM_DEFAULT_EXCHANGE_NAME, routingKey, JSON.toJSONString(message));
    }



    public void sendAsyncMsg(String msg, String routingKey) {
        IMMQMessage immqMessage = IMMQMessage.builder().body(msg).build();
        MessagePostProcessor processor = new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                String correlationId = UUID.randomUUID().toString();
                immqMessage.setMsgId(correlationId);
                message.getMessageProperties().setCorrelationId(correlationId);
                message.getMessageProperties().setContentEncoding("UTF-8");
                return message;
            }
        };

        asyncRabbitTemplate.convertSendAndReceive(IM_DEFAULT_EXCHANGE_NAME, routingKey, JSON.toJSONString(immqMessage), processor);


    }



    public void sendAsyncMsg(String msg, String routingKey,String exchangeName) {
        IMMQMessage immqMessage = IMMQMessage.builder().body(msg).build();
        MessagePostProcessor processor = new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                String correlationId = UUID.randomUUID().toString();
                immqMessage.setMsgId(correlationId);
                message.getMessageProperties().setCorrelationId(correlationId);
                message.getMessageProperties().setContentEncoding("UTF-8");
                return message;
            }
        };

        asyncRabbitTemplate.convertSendAndReceive(exchangeName, routingKey, JSON.toJSONString(immqMessage), processor);


    }

    /**
     * @param msg
     * @param routingKey
     * @param delayTime  延迟时间 单位毫秒
     * 这种方式是给第条message设置超时时间，会发生不一样的消息超时时间不按时执行的问题，建议要不使用队列设置x-message-ttl，要么固定设置message消息超时时间
     */
    public void sendMsgWithDelay(IMMQMessage msg, String routingKey, long delayTime) {
        MessagePostProcessor processor = new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setExpiration(delayTime + "");
                return message;
            }
        };
        rabbitTemplate.convertSendAndReceive(IM_DEAD_LETTER_PUSH_EXCHANGE, routingKey, JSON.toJSONString(msg), processor);
    }


    public void sendMsg(IMMQMessage msg, String routingKey){
        MessagePostProcessor processor = new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setContentEncoding("UTF-8");
                return message;
            }
        };
        rabbitTemplate.convertSendAndReceive(IM_DEAD_LETTER_PUSH_EXCHANGE, routingKey, JSON.toJSONString(msg), processor);
    }
}
