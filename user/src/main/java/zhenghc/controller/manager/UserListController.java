package zhenghc.controller.manager;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zhenghc.common.BaseConstants;
import zhenghc.common.resp.BaseResponse;
import zhenghc.entity.User;
import zhenghc.entity.dto.RegistUserDTO;
import zhenghc.mapper.UserMapper;

import java.util.List;

@RestController
@RequestMapping("/UserList")
public class UserListController {



    @Autowired
    private UserMapper userMapper;

    @PostMapping("/list")
    public BaseResponse list(){

        List<User> list = userMapper.selectAllByType(4);

        return BaseResponse.success(list);
    }

    @PostMapping("/managerList")
    public BaseResponse managerList(){

        List<User> list = userMapper.selectAll();

        return BaseResponse.success(list);
    }


    @PostMapping("/addManager")
    public BaseResponse addManager(@RequestBody User param){

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(user.getUserType() != 1){
            return BaseResponse.error(BaseConstants.resultCode.BUSINESS_ERROR, "权限非法");
        }
        param.setUserType(2);

        RegistUserDTO newUser = new RegistUserDTO();
        BeanUtils.copyProperties(param,newUser);


        userMapper.insertUser(newUser);

        return BaseResponse.success("添加成功", null);
    }

    @PostMapping("/deleteManager")
    public BaseResponse deleteManager(@RequestBody User param){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(user.getUserType() != 1){
            return BaseResponse.error(BaseConstants.resultCode.BUSINESS_ERROR, "权限非法");
        }

        userMapper.deleteById(param.getId());

        return BaseResponse.success("删除成功",null);
    }


    @PostMapping("/delete")
    public BaseResponse delete(@RequestBody User param){

        userMapper.deleteById(param.getId());

        return BaseResponse.success("删除成功",null);
    }

    @PostMapping("/updateUserType")
    public BaseResponse updateUserType(@RequestBody User param){

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(user.getUserType() != 1){
            return BaseResponse.error(BaseConstants.resultCode.BUSINESS_ERROR, "权限非法");
        }

        userMapper.update(param);

        return BaseResponse.success("修改成功", null);
    }

}
