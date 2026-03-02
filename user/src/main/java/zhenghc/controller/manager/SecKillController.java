package zhenghc.controller.manager;

import com.alibaba.fastjson.JSON;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zhenghc.common.BaseConstants;
import zhenghc.common.resp.BaseResponse;
import zhenghc.entity.*;
import zhenghc.mapper.CardMapper;
import zhenghc.mapper.MemberCardMapper;
import zhenghc.mapper.SeckillActivityMapper;
import zhenghc.mapper.SeckillOrderMapper;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/seckill")
public class SecKillController {


    @Autowired
    private SeckillActivityMapper seckillActivityMapper;

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;


    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final DefaultRedisScript<Long> seckillScript;

    static {
        seckillScript = new DefaultRedisScript<>();
        seckillScript.setLocation(new ClassPathResource("lua/seckill.lua"));
        seckillScript.setResultType(Long.class);
    }

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private MemberCardMapper memberCardMapper;
    @Autowired
    private CardMapper cardMapper;

    /**
     * 创建秒杀活动
     * */
    @PostMapping("/create")
    public BaseResponse create(@RequestBody SeckillActivity param){

        if(param.getSeckillPrice().compareTo(param.getOriginalPrice()) >= 0
                || !param.getStartTime().before(param.getEndTime())
                || param.getTotalStock() <= 0){
            return BaseResponse.error(BaseConstants.resultCode.BUSINESS_ERROR, "秒杀活动设置有误!");
        }

        param.setAvailableStock(param.getTotalStock());
        param.setStatus(0);

        seckillActivityMapper.insert(param);

        return BaseResponse.success("创建成功", null);
    }


    /**
     * 查询活动列表
     * */
    @PostMapping("/list")
    public BaseResponse list(){

        List<SeckillActivity> list = seckillActivityMapper.selectAll();

        for(SeckillActivity item : list){
            String stock = (String) redisTemplate.opsForValue().get("seckill:stock:" + item.getId());//因为存进去的时候就是以字符串的类型存的所以取得时候也得用字符串来接收
            if(stock != null){
                item.setAvailableStock(Integer.parseInt(stock));
            }
        }

         return BaseResponse.success(list);
    }

    /**
     * 开启活动/关闭活动
     * */
    @PostMapping("/toggle")
    public BaseResponse toggle(@RequestBody SeckillActivity param){

        SeckillActivity activity = seckillActivityMapper.selectById(param.getId());
        if(param.getStatus() == 1){
            if (activity.getStatus() != 0) {
                return BaseResponse.error("只有未进行的活动才能开启", null);
            }
            redisTemplate.opsForValue().set("seckill:stock:" + param.getId(), activity.getAvailableStock().toString());
            redisTemplate.opsForValue().set("seckill:started:" + param.getId(), "true");
            redisTemplate.delete("seckill:bought:" + param.getId());
        }else{
            // 只有进行中的活动才能关闭
            if (activity.getStatus() != 1) {
                return BaseResponse.error("只有进行中的活动才能关闭", null);
            }
            redisTemplate.delete("seckill:started:" + param.getId());
        }
        seckillActivityMapper.updateStatus(param.getId(), param.getStatus());

        return BaseResponse.success("开始秒杀", null);
    }

    @PostMapping("/delete")
    public BaseResponse delete(@RequestBody SeckillActivity param){

        SeckillActivity activity = seckillActivityMapper.selectById(param.getId());
        if(activity.getStatus() == 1){
            return BaseResponse.error(BaseConstants.resultCode.BUSINESS_ERROR, "进行中的活动不允许删除");
        }
        redisTemplate.delete("seckill:stock:" + param.getId());
        redisTemplate.delete("seckill:started:"+param.getId());
        redisTemplate.delete("seckill:bought:"+param.getId());
        seckillActivityMapper.delete(param.getId());

        return BaseResponse.success("删除成功", null);
    }

    @PostMapping("/buy")
    public BaseResponse buy(@RequestBody SeckillOrder param){

         String isStarted = (String) redisTemplate.opsForValue().get("seckill:started:"+param.getActivityId());
         if(!Boolean.valueOf(isStarted)){
             return BaseResponse.error(BaseConstants.resultCode.BUSINESS_ERROR, "活动还未开始!");
         }

        SeckillActivity seckillActivity = seckillActivityMapper.selectById(param.getActivityId());

        Card card = cardMapper.selectById(seckillActivity.getCardId());
        //当购买会员卡类型为时长卡时，查询用户是否有为过期的时长卡，存在则无法购买
        if(card.getCardCategory().equals("1")){
            List<MemberCard> cards = memberCardMapper.queryTimeCardByUserId(param.getUserId());
            if( cards != null && cards.size() >= 1){
                return BaseResponse.error(BaseConstants.resultCode.BUSINESS_ERROR, "不可重复购买时长卡");
            }
        }

         Long result = (Long)redisTemplate.execute(seckillScript,
                 Arrays.asList(
                         "seckill:stock:" + param.getActivityId(),
                         "seckill:bought:" + param.getActivityId()
                 ),
                 param.getUserId().toString()
         );

         if(result == -1) return BaseResponse.error(BaseConstants.resultCode.BUSINESS_ERROR,"您已参与过该活动");
         if(result == 0) return BaseResponse.error(BaseConstants.resultCode.BUSINESS_ERROR,"已抢光");



        String orderNo = "SK" + System.currentTimeMillis() + param.getUserId();
        SeckillMessage seckillMessage = new SeckillMessage();
        seckillMessage.setOrderNo(orderNo);
        seckillMessage.setActivityId(param.getActivityId());
        seckillMessage.setUserId(param.getUserId());
        seckillMessage.setSeckillPrice(seckillActivity.getSeckillPrice());
        seckillMessage.setCardId(seckillActivity.getCardId());
        seckillMessage.setCreateTime(LocalDateTime.now().toString());

        rabbitTemplate.convertAndSend("seckill.direct.exchange", "seckill.order", JSON.toJSONString(seckillMessage));
        rabbitTemplate.convertAndSend("seckill.direct.exchange", "seckill.delay", JSON.toJSONString(seckillMessage));


        Map<String,String> data = new HashMap<>();
        data.put("orderNo",orderNo);
        return BaseResponse.success(data);
    }


    @PostMapping("/pay")
    public BaseResponse pay(@RequestBody SeckillOrder param){

        SeckillOrder seckillOrder = seckillOrderMapper.selectByOrderNo(param.getOrderNo());
        if(seckillOrder.getStatus() != 1){
            return BaseResponse.error(BaseConstants.resultCode.BUSINESS_ERROR, "订单支付状态异常");
        }

        seckillOrderMapper.updateStatusByOrderNo(param.getOrderNo(),2);

        Card card = cardMapper.selectById(seckillOrder.getCardId());

        MemberCard memberCard = new MemberCard();
        memberCard.setUserId(param.getUserId());
        memberCard.setCardTypeId(seckillOrder.getCardId());
        memberCard.setCardNumber(String.valueOf(System.currentTimeMillis()));
        if(card.getCardCategory().equals("1")){
            memberCard.setDurationDays(card.getDurationDays());
            memberCard.setStartTime(new Date());
            memberCard.setEndTime(new Date(new Date().getTime() + card.getDurationDays() * 24L * 60 * 60 * 1000));
        }else if(card.getCardCategory().equals("2")){
            memberCard.setTotalTimes( card.getTotalTimes());
            memberCard.setRemainingTimes(card.getTotalTimes());
        }
        memberCard.setStatus(1);

        memberCardMapper.insert(memberCard);

        return BaseResponse.success("支付成功",null);
    }

    @PostMapping("/myOrders")
    public BaseResponse myOrders(@RequestBody SeckillOrder param){

        List<SeckillOrder> list = seckillOrderMapper.selectAllByUserId(param.getUserId());


        return BaseResponse.success(list);
    }





}
