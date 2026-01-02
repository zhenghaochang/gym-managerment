package zhenghc.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zhenghc.common.BaseConstants;
import zhenghc.common.properties.JwtProperties;
import zhenghc.common.util.JwtUtil;
import zhenghc.common.util.RedisUtil;
import zhenghc.entity.User;
import zhenghc.entity.dto.LoginDTO;
import zhenghc.entity.dto.RegistUserDTO;
import zhenghc.entity.vo.BaseResponse;
import zhenghc.entity.vo.LoginVo;
import zhenghc.mapper.UserMapper;
import zhenghc.service.EmailCodeService;
import zhenghc.service.MailService;
import zhenghc.service.UserService;
import zhenghc.service.impl.EmailCodeServiceImpl;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private EmailCodeService emailCodeService;

    @Autowired
    private MailService mailService;

    @Value("${sky.jwt.secret-key}")
    private String secretKey;
    @Value("${sky.jwt.time-mill}")
    private long timeMill;



    @PostMapping("/login")
    public BaseResponse login(@RequestBody LoginDTO loginDTO) {

        User user = userService.login(loginDTO.getUsername());

        if(!user.getPassword().equals(loginDTO.getPassword())){
            return BaseResponse.error("用户名或密码错误");
        }

        redisUtil.set(BaseConstants.USER_ID_KEY+user.getId(), user);

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        String token = JwtUtil.createJwt(claims, timeMill, secretKey);

        LoginVo loginVo = new LoginVo();
        loginVo.setUser(user);
        loginVo.setToken(token);

        return BaseResponse.success(loginVo);

    }

    @PostMapping("/register")
    public BaseResponse register(@RequestBody RegistUserDTO userDTO) {

        User returnUser = userMapper.selectUserByUserName(userDTO.getUsername());
        if(returnUser != null){
            return BaseResponse.error("用户已存在");
        }

        try{
            userService.regist(userDTO);
        }catch (Exception e){
            return BaseResponse.error("注册异常");
        }

        return BaseResponse.success(null);
    }

    @PostMapping("/getEmailCode")
    public BaseResponse getEmailCode(@RequestBody Map<String, String> body){

        String email = body.get("email");

        try{
            emailCodeService.createCode(email);
            mailService.sendRegistMail(email, "注册验证码", (String)redisUtil.getValue("email:"+email));
        } catch (Exception e) {
            throw new RuntimeException("获取验证码失败");
        }
        return BaseResponse.success("验证码已发送");
    }

}
