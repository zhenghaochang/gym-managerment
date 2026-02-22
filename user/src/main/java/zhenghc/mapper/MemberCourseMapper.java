package zhenghc.mapper;

import org.apache.ibatis.annotations.Mapper;
import zhenghc.entity.MemberCourse;
import zhenghc.entity.dto.MemberCourseDTO;
import zhenghc.entity.vo.PrivateBookingListVo;

import java.util.List;

@Mapper
public interface MemberCourseMapper {

    void insert(MemberCourseDTO param);

    List<MemberCourse> selectAll(Long userId);

    void updateStatus(Long courseId, Integer status);

    MemberCourse selectById(Long id);

    void updateStatusAndReason(Long memberCourseId, Integer status, String refuseReason);

    List<MemberCourse> selectByBooking(Long userId);

    List<PrivateBookingListVo> selectByBookingPrivate(Long userId);
}
