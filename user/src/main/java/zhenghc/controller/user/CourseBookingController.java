package zhenghc.controller.user;

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
import zhenghc.entity.dto.BookingDTO;
import zhenghc.entity.dto.CourseCancelDTO;
import zhenghc.entity.dto.ScheduleListDTO;
import zhenghc.entity.vo.BookedRCVo;
import zhenghc.entity.vo.PrivateBookingListVo;
import zhenghc.entity.vo.ScheduleBookedNumVo;
import zhenghc.entity.vo.ScheduleListVo;
import zhenghc.mapper.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/booking")
public class CourseBookingController {

    @Autowired
    private MemberCourseMapper memberCourseMapper;

    @Autowired
    private CourseScheduleMapper courseScheduleMapper;

    @Autowired
    private BookingRecordMapper bookingRecordMapper;

    @Autowired
    private BookingPrivateRecordMapper bookingPrivateRecordMapper;
    @Autowired
    private CourseAppFormMapper courseAppFormMapper;

    @PostMapping("/list")
    public BaseResponse list(){
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<MemberCourse> list = memberCourseMapper.selectByBooking(user.getId());

        return BaseResponse.success(list);
    }

    @PostMapping("/scheduleList")
    public BaseResponse scheduleList(@RequestBody ScheduleListDTO param){


        List<ScheduleListVo> list = courseScheduleMapper.selectAll();

        List<ScheduleBookedNumVo> numList = bookingRecordMapper.selectBookedNumByDate(param);

        // 查当前用户在该周已预约的 schedule_id 集合
        List<BookedRCVo> idList = bookingRecordMapper.selectUserBookedScheduleIds(param.getUserId(), param.getStartDate(), param.getEndDate());
        //将里面idList里面的scheduleId都取出来单独做成一个集合
        Map<Long, Long> bookedRecordMap = new HashMap<>();
        for (BookedRCVo item : idList) {
            bookedRecordMap.put(item.getScheduleId(), item.getBookingRecordId());
        }
        Map<Long,Integer> idAndStatusMap = new HashMap<>();
        for (BookedRCVo item : idList) {
            idAndStatusMap.put(item.getScheduleId(), item.getStatus());
        }
        //判断如果该排期（schedule）的id在已预约的scheduleId中
        list.forEach(x -> {

            if(bookedRecordMap.containsKey(x.getId())){
                x.setUserBooked(true);
                x.setBookingRecordId(bookedRecordMap.get(x.getId()));
                x.setRecordStatus(idAndStatusMap.get(x.getId()));
            }else{
                x.setUserBooked(false);
            }
        });


        list = list.stream().filter(x -> {
                for(ScheduleBookedNumVo item : numList){
                    if(item.getScheduleId().equals(x.getId())){
                        x.setBookedCount(item.getNumber());
                        break;
                    }
                }
            return true;
        }).collect(Collectors.toList());


        return BaseResponse.success(list);
    }


    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/booking")
    public BaseResponse booking(@RequestBody BookingDTO param){


        memberCourseMapper.updateStatus(param.getMemberCourseId(),2);

        bookingRecordMapper.insert(param);

        return BaseResponse.success("预约成功", null);
    }

    @PostMapping("/privateList")
    public BaseResponse privateList(){

        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<PrivateBookingListVo> list = memberCourseMapper.selectByBookingPrivate(user.getId());

        return BaseResponse.success(list);
    }

    @PostMapping("/bookingPrivate")
    public BaseResponse bookingPrivate(@RequestBody BookingPrivateRecord param){

        memberCourseMapper.updateStatus(param.getMemberCourseId(),2);

        bookingPrivateRecordMapper.insert(param);

        return BaseResponse.success("预约成功，稍后会有教练联系，请保持电话畅通",null);
    }

    /**
     * 团课取消预约接口
     * */
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/courseCancel")
    public BaseResponse courseCancel(@RequestBody CourseCancelDTO param){

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        BookingRecord record = bookingRecordMapper.selectById(param.getBookingRecordId());

        CourseAppForm form = new CourseAppForm();
        form.setMemberCourseId(record.getMemberCourseId());
        form.setRealName(param.getRealName());
        form.setFormNum(String.valueOf(System.currentTimeMillis()));
        form.setAppReason(param.getAppReason());
        form.setAppType(2);
        form.setStatus(1);
        form.setUserId(user.getId());
        courseAppFormMapper.insert(form);

        bookingRecordMapper.updateStatus(param.getBookingRecordId(),2);

        return BaseResponse.success("申请成功",null);
    }


}
