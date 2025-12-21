package zhenghc.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * 用户id
     * */
    private Long id;

    /**
     * 用户名
     * */
    private String username;

    /**
     * 密码
     **/
    private String password;

    /**
     * 邮箱
     * */
    private String email;

    /**
     * 电话号
     * */
    private String phone;

    /**
     * 真实姓名
     * */
    private String realName;
    /**
     * 昵称
     * */
    private String nickName;

    /**
     * 头像URL
     * */
    private String avatar;

    /**
     * 性别
     * */
    private Integer gender;

    /**
     * 生日
     * */
    private Date birthday;

    /**
     * 身高
     * */
    private BigDecimal height;

    /**
     * 体重
     * */
    private BigDecimal weight;

    /**
     * 用户类型 1-会员 2-教练 3-管理员 4-超级管理员
     * */
     private Integer userType;

    /**
     * 状态
     * */
    private Integer status;

    /**
     * 创建时间
     * */
    private Timestamp createTime;

    /**
     * 更新时间
     * */
    private Timestamp updateTime;

    /**
     * 权限列表
     */
    private List<String> permissions;

    /**
     * 权限集合（不序列化）
     */
    @JsonIgnore
    private Collection<? extends GrantedAuthority> authorities;

}
