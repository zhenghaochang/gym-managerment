package zhenghc.mapper;

import org.apache.ibatis.annotations.Mapper;
import zhenghc.entity.CardAppForm;

import java.util.List;

@Mapper
public interface CardAppFormMapper {

    void insert(CardAppForm param);

    List<CardAppForm> selectBystatus(String cardNum, Integer status, Integer appType);

    List<CardAppForm> selectList(CardAppForm param);

    void updateStatusByCardNum(CardAppForm param);
}
