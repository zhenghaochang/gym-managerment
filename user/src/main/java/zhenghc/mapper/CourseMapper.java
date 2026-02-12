package zhenghc.mapper;

import org.apache.ibatis.annotations.Mapper;
import zhenghc.entity.Course;
import zhenghc.entity.vo.CourseVo;

import java.util.List;

@Mapper
public interface CourseMapper {
    List<CourseVo> selectAll();
}
