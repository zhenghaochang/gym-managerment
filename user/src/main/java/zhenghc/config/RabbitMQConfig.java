package zhenghc.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    // 交换机
    @Bean
    public DirectExchange seckillExchange() {
        return new DirectExchange("seckill.direct.exchange");
    }

    // 订单队列（设置TTL和死信）
    @Bean
    public Queue seckillOrderQueue() {
//        Map<String, Object> args = new HashMap<>();
//        args.put("x-dead-letter-exchange", "seckill.direct.exchange");
//        args.put("x-dead-letter-routing-key", "seckill.dead");
////        args.put("x-message-ttl", 900000); // 15分钟
//        args.put("x-message-ttl", 300000);
        return new Queue("seckill.order.queue", true);
    }

    //延迟队列
    @Bean
    public Queue seckillDelayQueue(){
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", "seckill.direct.exchange");
        args.put("x-dead-letter-routing-key", "seckill.dead");
//        args.put("x-message-ttl", 900000); // 15分钟
        args.put("x-message-ttl", 300000);
        return new Queue("seckill.delay.queue", true, false, false, args);
    }



    // 死信队列
    @Bean
    public Queue seckillDeadQueue() {
        return new Queue("seckill.order.dead", true);
    }

    // 绑定
    @Bean
    public Binding orderBinding() {
        return BindingBuilder.bind(seckillOrderQueue())
                .to(seckillExchange()).with("seckill.order");
    }

    @Bean
    public Binding deadBinding() {
        return BindingBuilder.bind(seckillDeadQueue())
                .to(seckillExchange()).with("seckill.dead");
    }

    @Bean
    public Binding seckillDelayBinding(){
        return BindingBuilder.bind(seckillDelayQueue()).to(seckillExchange()).with("seckill.delay");
    }


}
