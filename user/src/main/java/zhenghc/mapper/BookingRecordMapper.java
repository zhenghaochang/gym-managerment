package zhenghc.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import zhenghc.entity.BookingPrivateRecord;
import zhenghc.entity.BookingRecord;
import zhenghc.entity.dto.BookingDTO;
import zhenghc.entity.dto.ScheduleListDTO;
import zhenghc.entity.vo.BookedRCVo;
import zhenghc.entity.vo.ScheduleBookedNumVo;

import java.util.List;

@Mapper
public interface BookingRecordMapper {
    List<ScheduleBookedNumVo> selectBookedNumByDate(@Param("param") ScheduleListDTO param);

    void insert(BookingDTO param);

    void insert(BookingPrivateRecord param);

    List<BookedRCVo> selectUserBookedScheduleIds(Long userId, String startDate, String endDate);

    BookingRecord selectById(Long bookingRecordId);

    void updateStatus(Long bookingRecordId, Integer status);

    Long selectIdByMcId(Long memberCourseId);
}
