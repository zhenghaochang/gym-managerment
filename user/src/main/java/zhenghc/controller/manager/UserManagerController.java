package zhenghc.controller.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zhenghc.common.resp.BaseResponse;
import zhenghc.entity.CardAppForm;
import zhenghc.entity.MemberCard;
import zhenghc.entity.vo.AppListVo;
import zhenghc.mapper.CardAppFormMapper;
import zhenghc.mapper.MemberCardMapper;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/userManager")
public class UserManagerController {

    @Autowired
    private CardAppFormMapper cardAppFormMapper;

    @Autowired
    private MemberCardMapper memberCardMapper;

    @PostMapping("/application/list")
    public BaseResponse appList(@RequestBody CardAppForm param){

        AppListVo appListVo = new AppListVo();

        List<CardAppForm> list = cardAppFormMapper.selectList(param);

        appListVo.setFormList(list);
        appListVo.setTotal(list.size());

        List<CardAppForm> newList = list.stream().filter(x -> x.getStatus() == 1).collect(Collectors.toList());

        appListVo.setPendingCount(newList.size());

        return BaseResponse.success(appListVo);
    }

    @PostMapping("/application/approval")
    public BaseResponse approval(@RequestBody CardAppForm param){


        try{
            cardAppFormMapper.updateStatusByCardNum(param);
        }catch (Exception e){
            throw new RuntimeException("申请单状态更新失败！");
        }

        if(param.getStatus() == 2){
            MemberCard card = memberCardMapper.selectByCardNum(param.getCardNum());
            Date endTime = card.getEndTime();
            LocalDate today = LocalDate.now();
            LocalDate endDate = endTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            long daysBetween = ChronoUnit.DAYS.between(today, endDate);
            card.setRemainingDays((int)daysBetween);

            memberCardMapper.updateStopByCardNum(param.getCardNum(),card.getRemainingDays());
        }


        return BaseResponse.success(null);
    }

}
