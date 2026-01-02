package zhenghc.entity.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Date;


@Builder
public class RegistUserDTO {
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
     * 用户类型: 1-会员 2-教练 3-管理员 4-超级管理员
     * */
    private Integer userType;

    /**
     * 邮箱验证码
     * */
    private String code;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public BigDecimal getHeight() {
        return height;
    }

    public void setHeight(BigDecimal height) {
        this.height = height;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
