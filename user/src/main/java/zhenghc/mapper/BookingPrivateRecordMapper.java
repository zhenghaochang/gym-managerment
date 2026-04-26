package zhenghc.mapper;

import org.apache.ibatis.annotations.Mapper;
import zhenghc.entity.BookingPrivateRecord;
import zhenghc.entity.BookingRecord;

import java.util.List;

@Mapper
public interface BookingPrivateRecordMapper {
    void insert(BookingPrivateRecord param);

    List<BookingPrivateRecord> selectAllByUserId(Long id);
}
