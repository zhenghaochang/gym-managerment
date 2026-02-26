package zhenghc.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zhenghc.common.BaseConstants;
import zhenghc.entity.Card;
import zhenghc.entity.User;
import zhenghc.entity.dto.CardDTO;
import zhenghc.common.resp.BaseResponse;
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

//        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        if(!cardDTO.getUserId().equals( user.getId())){
//            return BaseResponse.error( BaseConstants.resultCode.USER_ERROR,"用户权限非法");
//        }
        BaseResponse baseResponse;
        try{
            baseResponse = cardService.createOrder(cardDTO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return baseResponse;
    }

}
