package zhenghc.service.user;

import zhenghc.entity.Card;
import zhenghc.entity.dto.CardDTO;
import zhenghc.common.resp.BaseResponse;

import java.util.List;

public interface CardService {
    List<Card> getCardList();

    BaseResponse createOrder(CardDTO cardDTO);
}
