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
import zhenghc.entity.BookingPrivateRecord;
import zhenghc.entity.CourseSchedule;
import zhenghc.entity.MemberCourse;
import zhenghc.entity.User;
import zhenghc.entity.dto.BookingDTO;
import zhenghc.entity.dto.ScheduleListDTO;
import zhenghc.entity.vo.PrivateBookingListVo;
import zhenghc.entity.vo.ScheduleBookedNumVo;
import zhenghc.entity.vo.ScheduleListVo;
import zhenghc.mapper.BookingPrivateRecordMapper;
import zhenghc.mapper.BookingRecordMapper;
import zhenghc.mapper.CourseScheduleMapper;
import zhenghc.mapper.MemberCourseMapper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
        List<Long> bookedScheduleIds = bookingRecordMapper.selectUserBookedScheduleIds(param.getUserId(), param.getStartDate(), param.getEndDate());
        Set<Long> bookedSet = new HashSet<>(bookedScheduleIds);

        list.forEach(x -> {
            x.setUserBooked(bookedSet.contains(x.getId()));
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


}
