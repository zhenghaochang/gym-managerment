package zhenghc.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zhenghc.common.resp.BaseResponse;
import zhenghc.entity.User;
import zhenghc.mapper.UserMapper;

@RestController
@RequestMapping("/myCenter")
public class MyCenterController {

    @Autowired
    private UserMapper userMapper;

    @PostMapping("/list")
    public BaseResponse list(){

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User returnUser = userMapper.selectByUserId(user.getId());

        return BaseResponse.success(returnUser);
    }

    @PostMapping("/update")
    public BaseResponse update(@RequestBody User user){

        userMapper.update(user);

        return BaseResponse.success("修改成功", null);
    }

}
