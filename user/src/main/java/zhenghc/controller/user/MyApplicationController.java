package zhenghc.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zhenghc.common.resp.BaseResponse;
import zhenghc.entity.CourseAppForm;
import zhenghc.entity.User;
import zhenghc.mapper.CourseAppFormMapper;

import java.util.List;

@RestController
@RequestMapping("/myApplication")
public class MyApplicationController {

    @Autowired
    private CourseAppFormMapper courseAppFormMapper;

    @PostMapping("/list")
    public BaseResponse list(){

        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<CourseAppForm> list = courseAppFormMapper.select(user.getId());
        return BaseResponse.success(list);
    }

}
