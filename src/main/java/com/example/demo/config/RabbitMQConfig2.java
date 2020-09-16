package com.example.demo.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig2 {

    public static final String REPLY_QUEUE_NAME = "test.federation.queue.reply";
    public static final String REPLY_ROUTING_KEY = "test.federation.routingkey.reply";
    public static final String REPLY_EXCHANGE_NAME = "test.federation.exchange.reply";
    public static final String EVENT1_QUEUE_NAME = "test.topic.rbmq1.queue1";
    public static final String EVENT2_QUEUE_NAME = "test.fanout.rbmq2.queue2";
    public static final String EVENT3_QUEUE_NAME = "test.fanout.rbmq1.queue1";
    public static final String TEST_TOPIC_EXCHANGE = "test.topic";
    public static final String TEST_FANOUT_EXCHANGE = "test.fanout";


    @Bean
    AsyncRabbitTemplate asyncRabbitTemplate(RabbitTemplate rabbitTemplate, ConnectionFactory connectionFactory) {

        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setQueueNames(REPLY_QUEUE_NAME);
        AsyncRabbitTemplate template = new AsyncRabbitTemplate(rabbitTemplate,container);
        template.setReceiveTimeout(5000);

        return template;
    }

    @Bean("replyExchange")
    public DirectExchange replyExchange(){
        return new DirectExchange(REPLY_EXCHANGE_NAME);
    }

    @Bean("replyRabbitQueue")
    public Queue replyRabbitQueue(){
        return new Queue(REPLY_QUEUE_NAME);
    }

    @Bean
    public Binding replyRabbitBinding(@Qualifier("replyRabbitQueue") Queue queue,
                                     @Qualifier("replyExchange") DirectExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(REPLY_ROUTING_KEY);
    }





}
