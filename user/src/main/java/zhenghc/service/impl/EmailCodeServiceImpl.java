package zhenghc.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zhenghc.common.BaseConstants;
import zhenghc.common.util.RedisUtil;
import zhenghc.service.EmailCodeService;
import zhenghc.service.MailService;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class EmailCodeServiceImpl implements EmailCodeService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private MailService mailService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void createCode(String email) {
        Random random = new Random();
        String format = String.format("%06d", random.nextInt(1000000));

        try{
            redisUtil.setMin(BaseConstants.EMAIL_CODE_KEY+email, format, 5);
        } catch (Exception e) {
            throw new RuntimeException("redis异常");
        }
        //这里不发邮箱，只是生成验证码并存入reids里面
    }

    public void checkCode(String email, String code) {
        String redisCode = (String) redisUtil.getValue(BaseConstants.EMAIL_CODE_KEY +email);
        if(redisCode == null){
            throw new RuntimeException("验证码已过期");
        }
        if(!redisCode.equals(code)){
            throw new RuntimeException("验证码错误");
        }
    }

}
