package zhenghc.controller.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zhenghc.common.BaseConstants;
import zhenghc.common.resp.BaseResponse;
import zhenghc.entity.*;
import zhenghc.mapper.*;

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
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private CourseScheduleMapper courseScheduleMapper;

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


    @PostMapping("/courseSetting")
    public BaseResponse courseSetting(@RequestBody Course param){

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(user.getUserType() != 1 && user.getUserType() != 2){
            return BaseResponse.error(BaseConstants.resultCode.BUSINESS_ERROR, "权限非法");
        }

        courseMapper.update(param);

        return BaseResponse.success("修改成功",null);
    }

    @PostMapping("/courseAdd")
    public BaseResponse courseAdd(@RequestBody Course param){

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(user.getUserType() != 1 && user.getUserType() != 2 ){
            return BaseResponse.error(BaseConstants.resultCode.BUSINESS_ERROR, "权限非法");
        }

        courseMapper.insert(param);

        return BaseResponse.success("添加成功",null);
    }


    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/deleteCourse")
    public BaseResponse deleteCourse(@RequestBody Course param){

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(user.getUserType() != 1 || user.getUserType() != 2){
            return BaseResponse.error(BaseConstants.resultCode.BUSINESS_ERROR, "权限非法");
        }

        courseMapper.delete(param);
        courseScheduleMapper.deleteByCourseId(param.getId());

        return BaseResponse.success("删除成功",null);
    }


    @PostMapping("/scheduleUpdate")
    public BaseResponse scheduleUpdate(@RequestBody CourseSchedule param){

        courseScheduleMapper.update(param);

        return BaseResponse.success("修改成功",null);
    }

    @PostMapping("/deleteSchedule")
    public BaseResponse deleteSchedule(@RequestBody CourseSchedule param){

        courseScheduleMapper.delete(param);

        return BaseResponse.success("删除成功",null);
    }


    @PostMapping("/insertSchedule")
    public BaseResponse insertSchedule(@RequestBody CourseSchedule param){

        courseScheduleMapper.insert(param);

        return BaseResponse.success("添加成功", null);
    }

    @PostMapping("/BookingList")
    public BaseResponse bookingList(){

        List<BookingRecord> list = bookingRecordMapper.selectAll();

        return BaseResponse.success(list);
    }




}
