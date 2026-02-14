package zhenghc.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zhenghc.common.resp.BaseResponse;
import zhenghc.entity.MemberCourse;
import zhenghc.mapper.MemberCourseMapper;

@RestController
@RequestMapping("/myCourse")
public class MyCourseController {

    @Autowired
    private MemberCourseMapper memberCourseMapper;

    @PostMapping("/list")
    public BaseResponse list(){

        MemberCourse memberCourse = memberCourseMapper.selectAll();


        return null;
    }

}
