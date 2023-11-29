package com.example.demo.service;

import com.alibaba.fastjson.JSON;
import com.example.demo.bean.BizMQMessage;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

@Slf4j
public abstract class BaseEventReceiver {

    public void receive(Message message, Channel channel) throws IOException {
        log.info("BaseIMReceiver.receive,mq头信息：{}", message.getMessageProperties());
        log.info("收到的mq消息：{}", message.getBody());
        BizMQMessage bizMQMessage = null;
        String msg = message.getBody().toString();

        try {
            String body = new String(message.getBody(), "utf-8");
            bizMQMessage = JSON.parseObject(body, BizMQMessage.class);
            msg = bizMQMessage.getBody();
            log.info("接收转换的body消息：{}", msg);
        } catch (UnsupportedEncodingException e) {
            log.error("消息格式不正确，error msg:{}", message.getBody());
        }

        if (!StringUtils.hasText(msg)) {
            //告诉服务器收到这条消息 已经被我消费了 可以在队列删掉 这样以后就不会再发了
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            log.warn("转换mq的message出问题，将错误格式的消息从队列中清除!!,message:{}", message.getBody());
            return;
        }

        boolean ack = true;
        Exception exception = null;
        if (bizMQMessage.getRetrySize() < 3) {
            try {
                processing(msg);
            } catch (Exception e) {
                ack = false;
                exception = e;
            }
        } else {
            log.warn("此消息重试3次仍然请求失败!!,message:{}", msg);
            try {
                saveRetryFailMessage(msg);
            } catch (Exception e) {
                log.error("保存重试3次仍然失败的消息出错!!,message:{}", msg, e);
            }
        }

        if (!ack) {
            log.error("消息消费发生异常，error msg:{}", exception.getMessage(), exception);
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        } else {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }
    }

    /**
     * 业务消息内容，各实现类只关注各自业务
     *
     * @param body
     */
    protected abstract void processing(String body);

    /**
     * 保存重试失败
     *
     * @param body
     */
    protected abstract void saveRetryFailMessage(String body);
}
