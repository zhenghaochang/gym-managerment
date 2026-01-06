package zhenghc.mapper;

import org.apache.ibatis.annotations.Mapper;
import zhenghc.entity.Card;

import java.util.List;

@Mapper
public interface CardMapper {
    List<Card> selectList();

    Card selectById(Long cardId);
}
