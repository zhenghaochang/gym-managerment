package zhenghc.consumer;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import zhenghc.entity.SeckillMessage;
import zhenghc.entity.SeckillOrder;
import zhenghc.mapper.SeckillActivityMapper;
import zhenghc.mapper.SeckillOrderMapper;

import java.io.IOException;

@Component
public class SeckillDeathConsumer {

    @Autowired
    private SeckillActivityMapper seckillActivityMapper;

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @RabbitListener(queues = "seckill.order.dead")
    public void handleDeath(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {

        SeckillMessage seckillMessage = JSON.parseObject(message, SeckillMessage.class);

        SeckillOrder seckillOrder = seckillOrderMapper.selectByOrderNo(seckillMessage.getOrderNo());
        if(seckillOrder != null && seckillOrder.getStatus() == 1){
            seckillOrderMapper.updateStatusByOrderNo(seckillMessage.getOrderNo(),3);
            redisTemplate.opsForValue().increment("seckill:stock:"+seckillOrder.getActivityId());
            redisTemplate.opsForSet().remove("seckill:bought:"+seckillOrder.getActivityId(), seckillMessage.getUserId().toString());
            seckillActivityMapper.increaseStock(seckillMessage.getActivityId());
        }

        channel.basicAck(tag,false);


    }

}
