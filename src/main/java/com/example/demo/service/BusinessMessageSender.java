package com.example.demo.service;

import com.alibaba.fastjson.JSON;
import com.example.demo.bean.BizMQMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

import static com.example.demo.config.RabbitMQConfig2.BIZ_DEAD_LETTER_PUSH_EXCHANGE;
import static com.example.demo.config.RabbitMQConfig2.BIZ_DEFAULT_EXCHANGE_NAME;


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
        rabbitTemplate.setMandatory(true);
//        成功消费不会回调,没有正确路由到合适的队列，就会回调,此处默认交换机必须声明为延迟交换机，否则会报错
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
        BizMQMessage message = BizMQMessage.builder().body(msg).build();
        rabbitTemplate.convertSendAndReceive(BIZ_DEFAULT_EXCHANGE_NAME, routingKey, JSON.toJSONString(message));
    }



    public void sendAsyncMsg(String msg, String routingKey) {
        BizMQMessage bizMQMessage = BizMQMessage.builder().body(msg).build();
        MessagePostProcessor processor = buildMessageProcessor(bizMQMessage);
        asyncRabbitTemplate.convertSendAndReceive(BIZ_DEFAULT_EXCHANGE_NAME, routingKey, JSON.toJSONString(bizMQMessage), processor);

    }

    private static MessagePostProcessor buildMessageProcessor(BizMQMessage bizMQMessage) {
        MessagePostProcessor processor = new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                String correlationId = UUID.randomUUID().toString();
                bizMQMessage.setMsgId(correlationId);
                message.getMessageProperties().setCorrelationId(correlationId);
                message.getMessageProperties().setContentEncoding("UTF-8");
                return message;
            }
        };
        return processor;
    }


    public void sendAsyncMsg(String msg, String routingKey,String exchangeName) {
        BizMQMessage bizMQMessage = BizMQMessage.builder().body(msg).build();
        MessagePostProcessor processor = buildMessageProcessor(bizMQMessage);

        asyncRabbitTemplate.convertSendAndReceive(exchangeName, routingKey, JSON.toJSONString(bizMQMessage), processor);


    }

    /**
     * @param msg
     * @param routingKey
     * @param delayTime  延迟时间 单位毫秒
     * 这种方式是给第条message设置超时时间，会发生不一样的消息超时时间不按时执行的问题，建议要不使用队列设置x-message-ttl，要么固定设置message消息超时时间
     */
    public void sendMsgWithDelay(BizMQMessage msg, String routingKey, long delayTime) {
        MessagePostProcessor processor = new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setExpiration(delayTime + "");
                return message;
            }
        };
        rabbitTemplate.convertSendAndReceive(BIZ_DEAD_LETTER_PUSH_EXCHANGE, routingKey, JSON.toJSONString(msg), processor);
    }


    public void sendMsg(BizMQMessage msg, String routingKey){
        MessagePostProcessor processor = new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setContentEncoding("UTF-8");
                return message;
            }
        };
        rabbitTemplate.convertSendAndReceive(BIZ_DEAD_LETTER_PUSH_EXCHANGE, routingKey, JSON.toJSONString(msg), processor);
    }
}
