package zhenghc.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zhenghc.entity.Card;
import zhenghc.entity.dto.CardDTO;
import zhenghc.entity.vo.BaseResponse;
import zhenghc.service.user.CardService;

import java.util.List;

@RestController
@RequestMapping("/card")
public class CardController {

    @Autowired
    private CardService cardService;

    @PostMapping("/list")
    public BaseResponse list() {
        List<Card> cardList = cardService.getCardList();
        if(cardList == null || cardList.size() == 0){
            return BaseResponse.error("暂无数据");
        }
        return BaseResponse.success(cardList);
    }

    @PostMapping("/createOrder")
    public BaseResponse createOrder(@RequestBody CardDTO cardDTO) {

        try{
            cardService.createOrder(cardDTO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return BaseResponse.success("创建订单成功");
    }

}
