package io.qyi.e5.config.rabbitMQ;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: msgpush
 * @description:
 * @author: 落叶随风
 * @create: 2020-01-12 22:00
 **/
@Configuration
public class RabbitMQConfig {
    @Value("")
    private String DirectQueueName;
    /**
     * 处理死信队列的消费队列
     * */
    @Bean
    public Queue fanoutQueue1() {
        Map<String, Object> arguments = new HashMap<>(2);
        arguments.put("x-dead-letter-exchange", "delay");
        arguments.put("x-dead-letter-routing-key", "delay_key");
        return new Queue("delay_queue1", true, false, false, arguments);
    }
    /**
     *
     *创建消息处理队列
     */
    @Bean
    public Queue fanoutQueue2() {
        return new Queue("delay_queue2", true); // 队列持久
    }

    /**
     * 配置消息交换机
     * 针对消费者配置
     * FanoutExchange: 将消息分发到所有的绑定队列，无routingkey的概念
     * HeadersExchange ：通过添加属性key-value匹配
     * DirectExchange:按照routingkey分发到指定队列
     * TopicExchange:多关键字匹配
     * @return
     */
    @Bean
    public DirectExchange fanoutExchangeDelay() {
        return new DirectExchange("delay",true, false);
    }

    /*@Bean
    public FanoutExchange fanoutExchangeTencentMsg() {
        return new FanoutExchange(EXCHANGE);
    }*/

    //绑定  将队列和交换机绑定,
    @Bean
    public Binding bindingFanoutQueue1() {
        return BindingBuilder.bind(fanoutQueue1()).to(fanoutExchangeDelay()).with("delay");
    }
    @Bean
    public Binding bindingFanoutQueue2() {
        return BindingBuilder.bind(fanoutQueue2()).to(fanoutExchangeDelay()).with("delay_key");
    }



    //    无限循环问题
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(new Jackson2JsonMessageConverter());
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        return factory;
    }

}
