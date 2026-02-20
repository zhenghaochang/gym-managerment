package zhenghc.mapper;

import org.apache.ibatis.annotations.Mapper;
import zhenghc.entity.CourseAppForm;

import java.util.List;

@Mapper
public interface CourseAppFormMapper {
    void insert(CourseAppForm form);

    List<CourseAppForm> selectAll();

    void updateStatusByNum(String formNum, Integer status,String refuseReason);
}
