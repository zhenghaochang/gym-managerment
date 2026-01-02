package zhenghc.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zhenghc.common.BaseConstants;
import zhenghc.common.util.RedisUtil;
import zhenghc.entity.User;
import zhenghc.entity.dto.RegistUserDTO;
import zhenghc.entity.vo.BaseResponse;
import zhenghc.mapper.UserMapper;
import zhenghc.service.EmailCodeService;
import zhenghc.service.UserService;

import java.util.List;

@Slf4j
@Service
public class userServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private EmailCodeService emailCodeService;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public User login(String username) {

        User user = userMapper.selectUserByUserName(username);

        List<String> permissions = userMapper.selectPermissionsByUserId(user.getId());

        user.setPermissions(permissions);

        return user;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public BaseResponse regist(RegistUserDTO userDTO) {
        if(userDTO.getEmail() != null){
            User user = userMapper.selectUserByEmail(userDTO.getEmail());
            if(user != null){
                return BaseResponse.error("该邮箱已被注册");
            }
        }

        emailCodeService.checkCode(userDTO.getEmail(), userDTO.getCode());

        try{
            userMapper.insertUser(userDTO);
            userMapper.insertUserRole(userDTO.getId()); //将该用户加入到角色关联表
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("用户添加失败"+e.getMessage());
        }
        redisUtil.delete(BaseConstants.EMAIL_CODE_KEY +userDTO.getEmail());
        return BaseResponse.success("注册成功");
    }
}
