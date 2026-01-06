package zhenghc.mapper;

import org.apache.ibatis.annotations.Mapper;
import zhenghc.entity.dto.CardOrderDTO;

@Mapper
public interface CardOrderMapper {
    void insert(CardOrderDTO order);
}
