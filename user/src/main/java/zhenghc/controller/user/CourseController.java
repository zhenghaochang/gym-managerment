package zhenghc.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zhenghc.common.resp.BaseResponse;
import zhenghc.entity.Course;
import zhenghc.entity.vo.CourseVo;
import zhenghc.mapper.CourseMapper;

import java.util.List;

@RestController
@RequestMapping("/course")
public class CourseController {

    @Autowired
    private CourseMapper courseMapper;

    @PostMapping("/list")
    public BaseResponse list(){

        List<CourseVo> list = courseMapper.selectAll();

        return BaseResponse.success(list);
    }

}
