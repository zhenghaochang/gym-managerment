package zhenghc.controller.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zhenghc.common.resp.BaseResponse;
import zhenghc.entity.CourseAppForm;
import zhenghc.mapper.BookingRecordMapper;
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
    @Autowired
    private BookingRecordMapper bookingRecordMapper;

    @PostMapping("/list")
    public BaseResponse list(){

        List<CourseAppForm> list = courseAppFormMapper.selectAll();
        return BaseResponse.success(list);
    }

    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/updateStatus")
    public BaseResponse updateStatus(@RequestBody CourseAppForm param){

        courseAppFormMapper.updateStatusByNum(param.getFormNum(),param.getStatus(),param.getRefuseReason());

        if(param.getAppType() == 1){
            Integer status = 3;
            if(param.getStatus() == 3){
                status = 1;
                memberCourseMapper.updateStatusAndReason(param.getMemberCourseId(),status,param.getRefuseReason());
            }else{
                memberCourseMapper.updateStatus(param.getMemberCourseId(),status);
            }
        } else if (param.getAppType() == 2) {
            Long recordId = bookingRecordMapper.selectIdByMcId(param.getMemberCourseId());
            if(param.getStatus() == 2){
                memberCourseMapper.updateStatus(param.getMemberCourseId(), 1);
                bookingRecordMapper.updateStatus(recordId, 3);
            }else{
                bookingRecordMapper.updateStatus(recordId, 1);
            }
        }


        return BaseResponse.success("操作成功",null);
    }

}
