package zhenghc.mapper;

import org.apache.ibatis.annotations.Mapper;
import zhenghc.entity.SeckillOrder;

import java.util.List;

@Mapper
public interface SeckillOrderMapper {
    SeckillOrder selectByOrderNo(String orderNo);

    void insert(SeckillOrder seckillOrder);

    void updateStatusByOrderNo(String orderNo, int status);

    List<SeckillOrder> selectAllByUserId(Long userId);
}
