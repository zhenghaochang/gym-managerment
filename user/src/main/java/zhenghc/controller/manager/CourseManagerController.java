package zhenghc.controller.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zhenghc.common.resp.BaseResponse;
import zhenghc.entity.CourseAppForm;
import zhenghc.mapper.CourseAppFormMapper;
import zhenghc.mapper.MemberCourseMapper;

import java.util.List;

@RestController
@RequestMapping("/courseManager")
public class CourseManagerController {

    @Autowired
    private CourseAppFormMapper courseAppFormMapper;
    @Autowired
    private MemberCourseMapper memberCourseMapper;

    @PostMapping("/list")
    public BaseResponse list(){

        List<CourseAppForm> list = courseAppFormMapper.selectAll();
        return BaseResponse.success(list);
    }

    @PostMapping("/updateStatus")
    public BaseResponse updateStatus(@RequestBody CourseAppForm param){

        courseAppFormMapper.updateStatusByNum(param.getFormNum(),param.getStatus(),param.getRefuseReason());

        Integer status = 3;
        if(param.getStatus() == 3){
            status = 1;
            memberCourseMapper.updateStatusAndReason(param.getMemberCourseId(),status,param.getRefuseReason());
        }else{
            memberCourseMapper.updateStatus(param.getMemberCourseId(),status);
        }


        return BaseResponse.success("操作成功",null);
    }

}
