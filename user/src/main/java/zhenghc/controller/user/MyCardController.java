package zhenghc.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zhenghc.common.BaseConstants;
import zhenghc.entity.CardAppForm;
import zhenghc.entity.dto.MyCardListDTO;
import zhenghc.entity.MemberCard;
import zhenghc.entity.User;
import zhenghc.common.resp.BaseResponse;
import zhenghc.mapper.CardAppFormMapper;
import zhenghc.mapper.CardMapper;
import zhenghc.mapper.MemberCardMapper;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/myCard")
public class MyCardController {

    @Autowired
    private MemberCardMapper memberCardMapper;

    @Autowired
    private CardAppFormMapper cardAppFormMapper;

    @Autowired
    private CardMapper cardMapper;

    @PostMapping("/list")
    public BaseResponse list(@RequestBody MyCardListDTO myCardListDTO) {

        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!myCardListDTO.getUserId().equals(user.getId())){
            return BaseResponse.error(BaseConstants.resultCode.USER_ERROR,"用户权限非法");
        }

        List<MemberCard> list = memberCardMapper.listByUserId(user.getId());
        if(list == null || list.size() == 0){
            return BaseResponse.error(BaseConstants.resultCode.BUSINESS_ERROR, "用户无可用会员卡");
        }




        return BaseResponse.success("获取成功", list);
    }

    /**
     * 申请停卡
     * */
    @PostMapping("/appApproval")
    public BaseResponse appApproval(@RequestBody CardAppForm param){

        String formNum = String.valueOf(System.currentTimeMillis());

        param.setFormNum(formNum);
        param.setStatus(1);

        List<CardAppForm> list = cardAppFormMapper.selectBystatus(param.getCardNum(),param.getStatus(),param.getAppType());

        if(list.size() != 0){
            return BaseResponse.error(BaseConstants.resultCode.BUSINESS_ERROR, "请勿重复申请！");
        }

        cardAppFormMapper.insert(param);

        return BaseResponse.success("申请成功",null);

    }

}
