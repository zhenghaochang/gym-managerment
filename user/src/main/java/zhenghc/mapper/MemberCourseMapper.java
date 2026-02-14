package zhenghc.mapper;

import org.apache.ibatis.annotations.Mapper;
import zhenghc.entity.dto.MemberCourseDTO;

@Mapper
public interface MemberCourseMapper {

    void insert(MemberCourseDTO param);
}
