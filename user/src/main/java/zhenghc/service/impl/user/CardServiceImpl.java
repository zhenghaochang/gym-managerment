package zhenghc.service.impl.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zhenghc.entity.Card;
import zhenghc.entity.CardOrderItem;
import zhenghc.entity.dto.CardDTO;
import zhenghc.entity.dto.CardOrderDTO;
import zhenghc.mapper.CardMapper;
import zhenghc.mapper.CardOrderItemMapper;
import zhenghc.mapper.CardOrderMapper;
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
    public void createOrder(CardDTO cardDTO) {

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
        } catch (Exception e) {
            throw new RuntimeException("支付失败:"+e.getMessage());
        }




    }
}
