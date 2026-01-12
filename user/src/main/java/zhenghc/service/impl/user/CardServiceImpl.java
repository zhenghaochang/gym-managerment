package zhenghc.service.impl.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import zhenghc.common.BaseConstants;
import zhenghc.entity.Card;
import zhenghc.entity.CardOrderItem;
import zhenghc.entity.MemberCard;
import zhenghc.entity.User;
import zhenghc.entity.dto.CardDTO;
import zhenghc.entity.dto.CardOrderDTO;
import zhenghc.entity.dto.MyCardListDTO;
import zhenghc.entity.vo.BaseResponse;
import zhenghc.mapper.CardMapper;
import zhenghc.mapper.CardOrderItemMapper;
import zhenghc.mapper.CardOrderMapper;
import zhenghc.mapper.MemberCardMapper;
import zhenghc.service.user.CardService;

import java.util.Date;
import java.util.List;

@Service
public class CardServiceImpl implements CardService {

    @Autowired
    private CardMapper cardMapper;

    @Autowired
    private CardOrderMapper cardOrderMapper;
    @Autowired
    private CardOrderItemMapper cardOrderItemMapper;
    @Autowired
    private MemberCardMapper  memberCardMapper;

    @Override
    public List<Card> getCardList() {
        List<Card> cards;
        try{
            cards = cardMapper.selectList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return cards;
    }

    @Transactional(rollbackFor =  Exception.class)
    @Override
    public BaseResponse createOrder(CardDTO cardDTO) {

        String orderNum =  "HYK" + System.currentTimeMillis();

        try{
            Card card = cardMapper.selectById(cardDTO.getCardId());

            CardOrderDTO order = new CardOrderDTO();
            order.setOrderNum( orderNum);
            order.setUserId(cardDTO.getUserId());
            order.setOrderAmount(card.getPrice());
            order.setPaymentAmount(card.getPrice());
            order.setPaymentMethod(cardDTO.getPaymentMethod());
            order.setOrderStatus(2);
            order.setPaymentStatus(2);
            order.setPaymentTime(new Date());
            cardOrderMapper.insert(order);

            CardOrderItem cardItem = new CardOrderItem();
            cardItem.setOrderId( order.getId());
            cardItem.setProductId(cardDTO.getCardId());
            cardItem.setProductType(1);
            cardItem.setProductName(card.getCardName());
            cardItem.setProductPrice(card.getPrice());
            cardItem.setQuantity(1);
            cardItem.setSubtotal(card.getPrice());
            cardOrderItemMapper.insert(cardItem);

            //当购买会员卡类型为时长卡时，查询用户是否有为过期的时长卡，存在则无法购买
            if(card.getCardCategory().equals("1")){
                List<MemberCard> cards = memberCardMapper.queryTimeCardByUserId(cardDTO.getUserId());
                if( cards != null && cards.size() >= 1){
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return BaseResponse.error(BaseConstants.resultCode.BUSINESS_ERROR, "用户已存在时长卡");
                }
            }

            MemberCard memberCard = new MemberCard();
            memberCard.setUserId(cardDTO.getUserId());
            memberCard.setCardTypeId(cardDTO.getCardId());
            memberCard.setCardNumber(String.valueOf(System.currentTimeMillis()));
            if(card.getCardCategory().equals("1")){
                memberCard.setDurationDays(card.getDurationDays());
                memberCard.setStartTime(new Date());
                memberCard.setEndTime(new Date(new Date().getTime() + card.getDurationDays() * 24L * 60 * 60 * 1000));
            }else if(card.getCardCategory().equals("2")){
                memberCard.setTotalTimes( card.getTotalTimes());
                memberCard.setRemainingTimes(card.getTotalTimes());
            }
            memberCard.setStatus( 1);

            memberCardMapper.insert(memberCard);

            return  BaseResponse.success("购买成功");
        } catch (Exception e) {
            throw new RuntimeException("支付失败:"+e.getMessage());
        }




    }
}
