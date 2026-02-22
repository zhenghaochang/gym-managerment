package zhenghc.mapper;

import org.apache.ibatis.annotations.Mapper;
import zhenghc.entity.BookingPrivateRecord;

@Mapper
public interface BookingPrivateRecordMapper {
    void insert(BookingPrivateRecord param);
}
