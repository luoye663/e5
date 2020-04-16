package io.qyi.e5.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.Gson;
import io.qyi.e5.outlook.entity.Outlook;
import io.qyi.e5.outlook.service.IOutlookService;
import io.qyi.e5.service.task.ITask;
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

    @Autowired
    IOutlookService outlookService;

    @Autowired
    ITask Task;

    @GetMapping("/send")
    public void aaa() {
        Task.sendTaskOutlookMQ(19658189);
    }
}
