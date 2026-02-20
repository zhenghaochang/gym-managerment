package zhenghc.mapper;

import org.apache.ibatis.annotations.Mapper;
import zhenghc.entity.Order;
import zhenghc.entity.dto.CardOrderDTO;

@Mapper
public interface OrderMapper {
    void insert(CardOrderDTO order);

    void insertNew(Order order);

    Order selectByOrderNum(String orderNum);
}
