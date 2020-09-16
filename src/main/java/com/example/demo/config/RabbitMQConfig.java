package com.example.demo.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

//@Configuration
public class RabbitMQConfig {

    public static final String IM_DEFAULT_EXCHANGE_NAME = "test.im.exchange.push";
    public static final String IM_PUSH_QUEUE_NAME = "test.im.queue.push";
    public static final String IM_PUSH_ROUTING_KEY = "test.im.routingkey.push.*";
    public static final String IM_DEAD_LETTER_QUEUE_NAME = "test.im.queue.deadLetterPush";
    public static final String IM_DEAD_LETTER_PUSH_EXCHANGE = "test.im.exchange.deadLetterPush";
    private static final String IM_DEAD_LETTER_ROUTING_KEY = "test.im.routingkey.deadLetterPush";
    private static final String IM_DELAY_QUEUE_NAME = "test.im.queue.delayPush";
    public static final String IM_DELAY_ROUTING_KEY = "test.im.routingkey.delayPush";
    private static final String IM_DELAY_PUSH_ROUTING_KEY = "test.im.routingkey.push.delay";
    public static final String REPLY_QUEUE_NAME = "test.im.queue.reply";
    public static final String REPLY_ROUTING_KEY = "test.im.routingkey.reply";
    public static final String REPLY_EXCHANGE_NAME = "test.im.exchange.reply";




//    @Bean
//    public ConnectionFactory rabbitConnectionFactory() {
//        CachingConnectionFactory connectionFactory =
//                new CachingConnectionFactory("10.1.249.250");
//        connectionFactory.setUsername("htone");
//        connectionFactory.setPassword("htone");
//        connectionFactory.setVirtualHost("HTone");
//        connectionFactory.setPort(5672);
//        connectionFactory.getPublisherConnectionFactoryCacheProperties()
//        return connectionFactory;
//    }

    @Bean
    AsyncRabbitTemplate asyncRabbitTemplate(RabbitTemplate rabbitTemplate, ConnectionFactory connectionFactory) {

        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setQueueNames(REPLY_QUEUE_NAME);
//        rabbitTemplate.setRoutingKey(REPLY_ROUTING_KEY);
//        rabbitTemplate.setExchange(REPLY_EXCHANGE_NAME);
//        rabbitTemplate.setReplyAddress(REPLY_ROUTING_KEY);
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


    // 声明死信Exchange
    @Bean("imDeadLetterPushExchange")
    public DirectExchange imDeadLetterPushExchange(){
        return new DirectExchange(IM_DEAD_LETTER_PUSH_EXCHANGE);
    }

    // 声明imPush死信队列
    @Bean("imDeadLetterQueue")
    public Queue imDeadLetterQueue(){
        Map<String, Object> args = new HashMap<>(2);
        args.put("x-max-length", 60000);
        return QueueBuilder.durable(IM_DEAD_LETTER_QUEUE_NAME).withArguments(args).build();
    }


    // 声明IMExchange
    @Bean("imPushExchange")
    public TopicExchange imPushExchange(){
        return new TopicExchange(IM_DEFAULT_EXCHANGE_NAME);
    }

    // 声明imPush队列
    @Bean("imPushQueue")
    public Queue imPushQueue(){
        Map<String, Object> args = new HashMap<>(2);
//       x-dead-letter-exchange    这里声明当前队列绑定的死信交换机
        args.put("x-dead-letter-exchange", IM_DEAD_LETTER_PUSH_EXCHANGE);
//       x-dead-letter-routing-key  这里声明当前队列的死信路由key
        args.put("x-dead-letter-routing-key", IM_DEAD_LETTER_ROUTING_KEY);

        args.put("x-max-length", 100000);
        return QueueBuilder.durable(IM_PUSH_QUEUE_NAME).withArguments(args).build();
    }


    // 声明push业务队列绑定关系
    @Bean
    public Binding imPushBinding(@Qualifier("imPushQueue") Queue queue,
                                    @Qualifier("imPushExchange") TopicExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(IM_PUSH_ROUTING_KEY);
    }

    // 声明push死信队列绑定关系
    @Bean
    public Binding deadLetterBinding(@Qualifier("imDeadLetterQueue") Queue queue,
                                    @Qualifier("imDeadLetterPushExchange") DirectExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(IM_DEAD_LETTER_ROUTING_KEY);
    }



    /**
     * 如果delayQueue中的消息超时，则重新会到业务队列中
     * @return
     */
    @Bean("imDelayQueue")
    public Queue imDelayQueue(){
        Map<String, Object> args = new HashMap<>(2);
//       x-dead-letter-exchange    这里声明当前队列绑定到业务交换机
        args.put("x-dead-letter-exchange", IM_DEFAULT_EXCHANGE_NAME);
//       x-dead-letter-routing-key  这里声明当前队列的死信路由key:使用业务队列
        args.put("x-dead-letter-routing-key", IM_DELAY_PUSH_ROUTING_KEY);
        args.put("durable", true);
        args.put("x-message-ttl", 3000);
        args.put("x-max-length", 50000);
        return QueueBuilder.durable(IM_DELAY_QUEUE_NAME).withArguments(args).build();
    }

    @Bean
    public Binding imDelayQueueBinding(@Qualifier("imDelayQueue") Queue queue,
                                       @Qualifier("imDeadLetterPushExchange") DirectExchange exchange){
        //使用死信队列的交换机机，和死信队列共用一个交换机机
        return BindingBuilder.bind(queue).to(exchange).with(IM_DELAY_ROUTING_KEY);
    }

}
