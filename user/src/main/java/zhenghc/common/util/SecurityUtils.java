package zhenghc.common.util;
import zhenghc.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Security工具类
 */
public class SecurityUtils {

    /**
     * 获取用户
     */
    public static User getLoginUser() {
        try {
            return (User) getAuthentication().getPrincipal();
        } catch (Exception e) {
            throw new RuntimeException("获取用户信息异常");
        }
    }

    /**
     * 获取Authentication
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

}