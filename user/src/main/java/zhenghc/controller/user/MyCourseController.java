package zhenghc.controller.user;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zhenghc.common.BaseConstants;
import zhenghc.common.resp.BaseResponse;
import zhenghc.entity.*;
import zhenghc.entity.dto.DropClassDTO;
import zhenghc.entity.vo.MyCourseVo;
import zhenghc.mapper.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/myCourse")
public class MyCourseController {

    @Autowired
    private MemberCourseMapper memberCourseMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CourseAppFormMapper courseAppFormMapper;

    @PostMapping("/list")
    public BaseResponse list(){

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<MemberCourse> memberCourseList = memberCourseMapper.selectAll(user.getId());
        List<MyCourseVo> myCourseVoList = new ArrayList<>();
        for(MemberCourse memberCourse : memberCourseList){
            MyCourseVo myCourseVo = new MyCourseVo();
            BeanUtils.copyProperties(memberCourse,myCourseVo);
            Course course = courseMapper.selectByCourseId(memberCourse.getCourseId());
            myCourseVo.setCourseType(course.getCourseType());
            myCourseVo.setCoachId(course.getCoachId());
            myCourseVo.setDuration(course.getDuration());
            myCourseVo.setPrice(course.getPrice());

            Order order = orderMapper.selectByOrderNum(memberCourse.getOrderNum());
            User coach = userMapper.selectByUserId(myCourseVo.getCoachId());
            myCourseVo.setPaymentTime(order.getPaymentTime());
            myCourseVo.setCoachName(coach.getRealName());

            myCourseVoList.add(myCourseVo);
        }


        return BaseResponse.success(myCourseVoList);

    }

    /**
     * 退课接口
     * */
    @PostMapping("/dropClass")
    public BaseResponse dropClass(@RequestBody DropClassDTO param){

        MemberCourse thisCourse = memberCourseMapper.selectById(param.getMemberCourseId());
        if(thisCourse.getStatus() == 3){
            return BaseResponse.error(BaseConstants.resultCode.BUSINESS_ERROR , "请勿重复申请");
        }else if(thisCourse.getStatus() == 2){
            return BaseResponse.error(BaseConstants.resultCode.BUSINESS_ERROR , "该课程已签到，不可退课");
        }

//        memberCourseMapper.updateStatus(param.getId(),3);

        CourseAppForm form = new CourseAppForm();
        form.setFormNum(String.valueOf(System.currentTimeMillis()));
        form.setAppType(1);
        form.setStatus(1);
        form.setAppReason(param.getAppReason());
        form.setMemberCourseId(param.getMemberCourseId());
        form.setRealName(param.getRealName());
        courseAppFormMapper.insert(form);

        memberCourseMapper.updateStatus(param.getMemberCourseId(),4);

        return BaseResponse.success("申请成功", null);
    }

}
