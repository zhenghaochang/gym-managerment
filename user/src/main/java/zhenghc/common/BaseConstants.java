package zhenghc.common;

import org.springframework.stereotype.Component;

public class BaseConstants {

    public static final String USER_ID_KEY = "userId";

    public interface mailType{
        String REGIST = "注册账号";
        String FORGET_PASSWORD = "找回密码";
    }

    public static final String EMAIL_CODE_KEY = "email:";

}
