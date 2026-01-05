package zhenghc.service.impl.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zhenghc.entity.Card;
import zhenghc.entity.dto.CardDTO;
import zhenghc.entity.dto.CardOrderDTO;
import zhenghc.mapper.CardMapper;
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

            CardOrderDTO order = CardOrderDTO.builder()
                    .orderNum(orderNum)
                    .userId(cardDTO.getUserId())
                    .orderAmount(card.getPrice())
                    .paymentAmount(card.getPrice())
                    .paymentMethod(cardDTO.getPaymentMethod())
                    .paymentStatus(2)//支付成功
                    .orderStatus(2)//已完成
                    .paymentTime(new Date()).build();
            cardOrderMapper.insert(order);

        } catch (Exception e) {
            throw new RuntimeException("支付失败:"+e.getMessage());
        }




    }
}
