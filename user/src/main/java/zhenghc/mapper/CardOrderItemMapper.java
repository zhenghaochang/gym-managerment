package zhenghc.mapper;

import org.apache.ibatis.annotations.Mapper;
import zhenghc.entity.CardOrderItem;

@Mapper
public interface CardOrderItemMapper {
    void insert(CardOrderItem cardItem);
}
