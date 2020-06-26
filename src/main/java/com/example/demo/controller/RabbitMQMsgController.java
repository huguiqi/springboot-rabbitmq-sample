package com.example.demo.controller;

import com.example.demo.bean.IMMQMessage;
import com.example.demo.service.BusinessMessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("rabbitmq")
@RestController
public class RabbitMQMsgController {

    @Autowired
    private BusinessMessageSender sender;

    @RequestMapping("sendmsg")
    public void sendMsg(String msg){
        IMMQMessage immqMessage = new IMMQMessage<String>();
        immqMessage.setBody(msg);
        sender.sendMsg(immqMessage,"htone.im.routingkey.push.htoneMsg");
    }
}
