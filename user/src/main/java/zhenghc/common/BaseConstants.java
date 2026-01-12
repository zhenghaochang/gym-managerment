package zhenghc.common;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseConstants {

    public static final String USER_ID_KEY = "userId";

    public interface mailType{
        String REGIST = "注册账号";
        String FORGET_PASSWORD = "找回密码";
    }

    public static final String EMAIL_CODE_KEY = "email:";

    public interface resultCode{

        /**
         * 成功
         * */
        String SUCCESS = "00";
        /**
         * 失败
         * */
        String FAIL = "99";

        /**
         * 业务逻辑错误
         * */
        String BUSINESS_ERROR = "01";

        /**
         * 用户权限非法
         * */
        String USER_ERROR = "11";

    }

}
