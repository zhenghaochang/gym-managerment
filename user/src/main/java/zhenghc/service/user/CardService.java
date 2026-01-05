package zhenghc.service.user;

import zhenghc.entity.Card;
import zhenghc.entity.dto.CardDTO;

import java.util.List;

public interface CardService {
    List<Card> getCardList();

    void createOrder(CardDTO cardDTO);
}
