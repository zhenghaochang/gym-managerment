package zhenghc.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zhenghc.common.BaseConstants;
import zhenghc.entity.dto.MyCardListDTO;
import zhenghc.entity.MemberCard;
import zhenghc.entity.User;
import zhenghc.entity.vo.BaseResponse;
import zhenghc.mapper.MemberCardMapper;

import java.util.List;

@RestController
@RequestMapping("/myCard")
public class MyCardController {

    @Autowired
    private MemberCardMapper memberCardMapper;

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

}
