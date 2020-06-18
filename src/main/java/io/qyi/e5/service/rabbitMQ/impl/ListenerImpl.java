package io.qyi.e5.service.rabbitMQ.impl;

import com.rabbitmq.client.Channel;
import io.qyi.e5.outlook.service.IOutlookService;
import io.qyi.e5.service.task.ITask;
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
    @Autowired
    ITask Task;

    @RabbitHandler
    @RabbitListener(queues = "delay_queue3", containerFactory = "rabbitListenerContainerFactory")
    public void listen(Message message, Channel channel) throws IOException {
        logger.info("消费者1开始处理消息： {},时间戳:{}" ,message,System.currentTimeMillis());
        System.out.println("消费者1开始处理消息："+System.currentTimeMillis());
        int github_id = Integer.valueOf(new String(message.getBody()));
        Task.executeE5(github_id);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
        /*再次进行添加任务*/
        Task.sendTaskOutlookMQ(github_id);
    }
}
