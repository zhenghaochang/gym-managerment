package zhenghc.mapper;

import org.apache.ibatis.annotations.Mapper;
import zhenghc.entity.OrderItem;

@Mapper
public interface OrderItemMapper {
    void insert(OrderItem cardItem);
}
