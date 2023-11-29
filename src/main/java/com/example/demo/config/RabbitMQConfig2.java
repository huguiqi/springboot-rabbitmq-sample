package com.example.demo.config;

import com.example.demo.service.counter.TotalCounterSendMQ;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class RabbitMQConfig2 {

    public static final String REPLY_QUEUE_NAME = "test.federation.queue.reply";
    public static final String REPLY_ROUTING_KEY = "test.federation.routingkey.reply";
    public static final String REPLY_EXCHANGE_NAME = "test.federation.exchange.reply";
    public static final String EVENT1_QUEUE_NAME = "test.direct.rbmq1.queue3";
//    public static final String EVENT2_QUEUE_NAME = "test.fanout.rbmq1.queue1";
//    public static final String EVENT3_QUEUE_NAME = "test.fanout.rbmq1.queue2";
//    public static final String EVENT4_QUEUE_NAME = "test.topic.rbmq1.queue1";
//    public static final String EVENT5_QUEUE_NAME = "test.topic.rbmq2.queue2";
    public static final String EVENT2_QUEUE_NAME = "test.federation.rbmq2.fanout.queue2";
    public static final String EVENT3_QUEUE_NAME = "test.federation.rbmq2.fanout";
    public static final String EVENT4_QUEUE_NAME = "test.federation.rbmq2.topic.queue1";
    public static final String EVENT5_QUEUE_NAME = "test.federation.rbmq2.topic.queue2";
    public static final String TEST_TOPIC_EXCHANGE = "test.topic";
//    public static final String TEST_FANOUT_EXCHANGE = "test.fanout";
    public static final String TEST_FANOUT_EXCHANGE = "test.federation.rbmq2.fanout";
    public static final String BIZ_DEAD_LETTER_PUSH_QUEUE_NAME = "test.biz.queue.deadLetterPush";
    public static final String BIZ_DELAY_ROUTING_KEY = "";
    public static final String BIZ_DEAD_LETTER_PUSH_EXCHANGE = "test.biz.exchange.deadLetterPush";
    public static final String BIZ_DEFAULT_EXCHANGE_NAME = "test.biz.exchange.defaultPush";


    @Bean
    AsyncRabbitTemplate asyncRabbitTemplate(RabbitTemplate rabbitTemplate, ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setQueueNames(REPLY_QUEUE_NAME);
        container.setAutoStartup(true);
        container.setAutoDeclare(false);
        container.setRecoveryInterval(1000L);
        rabbitTemplate.setConfirmCallback((correlationData, ack, error) -> {
            log.info("消息唯一标识：" + correlationData);
            log.info("确认结果：" + ack);
            TotalCounterSendMQ counterSendMQ = TotalCounterSendMQ.getInstance();
            if (ack) {
                counterSendMQ.addCounter();
                log.info("消息确认发送成功!!,msgId:{}", correlationData);
            } else {
                log.warn("失败原因：{}", error);
            }
        });
        rabbitTemplate.setMandatory(true);

        AsyncRabbitTemplate template = new AsyncRabbitTemplate(rabbitTemplate,container);
        template.setReceiveTimeout(5000);
        template.setMandatory(true);
//        template.setEnableConfirms(true);
        return template;
    }

//    @Bean("replyExchange")
//    public DirectExchange replyExchange(){
//        return new DirectExchange(REPLY_EXCHANGE_NAME);
//    }
//
    @Bean("replyRabbitQueue")
    public Queue replyRabbitQueue(){
        return new Queue(REPLY_QUEUE_NAME);
    }
//
//    @Bean
//    public Binding replyRabbitBinding(@Qualifier("replyRabbitQueue") Queue queue,
//                                     @Qualifier("replyExchange") DirectExchange exchange){
//        return BindingBuilder.bind(queue).to(exchange).with(REPLY_ROUTING_KEY);
//    }





}
