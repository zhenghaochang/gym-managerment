package zhenghc.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import zhenghc.entity.BookingPrivateRecord;
import zhenghc.entity.dto.BookingDTO;
import zhenghc.entity.dto.ScheduleListDTO;
import zhenghc.entity.vo.ScheduleBookedNumVo;

import java.util.List;

@Mapper
public interface BookingRecordMapper {
    List<ScheduleBookedNumVo> selectBookedNumByDate(@Param("param") ScheduleListDTO param);

    void insert(BookingDTO param);

    void insert(BookingPrivateRecord param);

    List<Long> selectUserBookedScheduleIds(Long userId, String startDate, String endDate);
}
