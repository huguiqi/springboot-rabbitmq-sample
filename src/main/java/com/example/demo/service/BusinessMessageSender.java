package com.example.demo.service;

import com.alibaba.fastjson.JSON;
import com.example.demo.bean.IMMQMessage;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static com.example.demo.config.RabbitMQConfig.IM_DEAD_LETTER_PUSH_EXCHANGE;
import static com.example.demo.config.RabbitMQConfig.IM_DEFAULT_EXCHANGE_NAME;


@Component
public class BusinessMessageSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;



    @PostConstruct
    public void init() {
        rabbitTemplate.setConfirmCallback((correlationData, ack, error) -> {
            System.out.println("消息唯一标识：" + correlationData);
            System.out.println("确认结果：" + ack);
            System.out.println("失败原因：" + error);
        });
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            System.out.println("消息主体 message : " + message);
            System.out.println("响应 code : " + replyCode);
            System.out.println("描述：" + replyText);
            System.out.println("消息使用的交换器 exchange : " + exchange);
            System.out.println("消息使用的路由键 routing : " + routingKey);
        });
    }

    public void sendMsg(IMMQMessage msg,String routingKey){
        rabbitTemplate.convertSendAndReceive(IM_DEFAULT_EXCHANGE_NAME, routingKey, JSON.toJSONString(msg));
    }

    /**
     *
     * @param msg
     * @param routingKey
     * @param delayTime 延迟时间 单位毫秒
     */
    public void sendMsgWithDelay(IMMQMessage msg, String routingKey, long delayTime) {
        MessagePostProcessor processor = new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setExpiration(delayTime + "");
                return message;
            }
        };
        rabbitTemplate.convertSendAndReceive(IM_DEAD_LETTER_PUSH_EXCHANGE, routingKey, JSON.toJSONString(msg),processor);
    }
}
