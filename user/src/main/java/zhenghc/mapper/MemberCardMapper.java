package zhenghc.mapper;

import org.apache.ibatis.annotations.Mapper;
import zhenghc.entity.MemberCard;

import java.util.List;

@Mapper
public interface MemberCardMapper {

    void insert(MemberCard memberCard);

    List<MemberCard> listByUserId(Long userId);

    List<MemberCard> queryTimeCardByUserId(Long userId);

    MemberCard selectByCardNum(String cardNum);

    /**
     * 根据卡号冻结
     * */
    void updateStopByCardNum(String cardNum, Integer remainingDays);

    void updateStatusBatch(List<MemberCard> list);
}
