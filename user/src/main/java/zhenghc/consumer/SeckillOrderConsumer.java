package zhenghc.consumer;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import zhenghc.entity.SeckillMessage;
import zhenghc.entity.SeckillOrder;
import zhenghc.mapper.SeckillActivityMapper;
import zhenghc.mapper.SeckillOrderMapper;

import java.io.IOException;

@Component
public class SeckillOrderConsumer {

    @Autowired
    private SeckillActivityMapper seckillActivityMapper;

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @RabbitListener(queues = "seckill.order.queue")
    public void handleOrder(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {

        try{
            SeckillMessage seckillMessage = JSON.parseObject(message, SeckillMessage.class);

            if(seckillOrderMapper.selectByOrderNo(seckillMessage.getOrderNo()) != null){
                channel.basicAck(tag, false);
                return;
            }

            try{
                SeckillOrder seckillOrder = new SeckillOrder();
                seckillOrder.setActivityId(seckillMessage.getActivityId());
                seckillOrder.setSeckillPrice(seckillMessage.getSeckillPrice());
                seckillOrder.setOrderNo(seckillMessage.getOrderNo());
                seckillOrder.setCardId(seckillMessage.getCardId());
                seckillOrder.setUserId(seckillMessage.getUserId());
                seckillOrder.setStatus(1);
                seckillOrderMapper.insert(seckillOrder);
            } catch (Exception e) {
                throw new RuntimeException("插入语句有问题！！！！：",e);
            }

            seckillActivityMapper.decreaseStock(seckillMessage.getActivityId());

            channel.basicAck(tag,false);
        } catch (Exception e) {
//            channel.basicNack(tag,false,true);//重新入队
            throw new RuntimeException(e);
        }

    }


}
