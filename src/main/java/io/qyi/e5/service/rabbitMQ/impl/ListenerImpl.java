package io.qyi.e5.service.rabbitMQ.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import io.qyi.e5.outlook.service.IOutlookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @program: msgpush
 * @description:
 * @author: 落叶随风
 * @create: 2020-01-13 23:35
 **/
@Service
public class ListenerImpl {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    IOutlookService outlookService;

    @RabbitHandler
    @RabbitListener(queues = "delay_queue2", containerFactory = "rabbitListenerContainerFactory")
    public void listen(Message message, Channel channel) throws IOException {
        logger.info("消费者1开始处理消息： {},时间戳:{}" ,new String(message.getBody()),System.currentTimeMillis());
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
    }
}
