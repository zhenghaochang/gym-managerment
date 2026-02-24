package zhenghc.mapper;

import org.apache.ibatis.annotations.Mapper;
import zhenghc.entity.Order;
import zhenghc.entity.dto.CardOrderDTO;
import zhenghc.entity.vo.OrderListVo;

import java.util.List;

@Mapper
public interface OrderMapper {
    void insert(CardOrderDTO order);

    void insertNew(Order order);

    Order selectByOrderNum(String orderNum);

    List<OrderListVo> selectAllOrderByUserId(Long userId);
}
