package zhenghc.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import zhenghc.entity.User;
import zhenghc.entity.dto.RegistUserDTO;

import java.util.List;

@Mapper
public interface UserMapper {
    User selectUserByUserName(String username);

    List<String> selectPermissionsByUserId(Long id);

    void insertUser(@Param("userDTO") RegistUserDTO userDTO);

    User selectUserByEmail(String email);

    void insertUserRole(Long id);
}
