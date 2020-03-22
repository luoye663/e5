package io.qyi.e5.controller;

import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @program: e5
 * @description:
 * @author: 落叶随风
 * @create: 2020-03-16 01:01
 **/
@Controller
@RestController
public class TestController {
    @Autowired
    RabbitTemplate rabbitTemplate;

    @GetMapping("/send")
    public void aaa() {
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());


        rabbitTemplate.convertAndSend("delay", "delay", "ttt", message -> {
            MessageProperties messageProperties = message.getMessageProperties();
            // 设置这条消息的过期时间
            messageProperties.setExpiration("5000");
            return message;
        }, correlationData);
    }
}
