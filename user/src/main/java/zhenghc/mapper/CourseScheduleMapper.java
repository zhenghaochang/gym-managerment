package zhenghc.mapper;

import org.apache.ibatis.annotations.Mapper;
import zhenghc.entity.CourseSchedule;
import zhenghc.entity.vo.ScheduleListVo;

import java.util.List;

@Mapper
public interface CourseScheduleMapper {
    List<ScheduleListVo> selectAll();

    void update(CourseSchedule param);

    void deleteByCourseId(Long courseId);

    void delete(CourseSchedule param);
}
