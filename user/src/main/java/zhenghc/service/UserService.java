package zhenghc.service;

import zhenghc.entity.User;
import zhenghc.entity.dto.RegistUserDTO;
import zhenghc.entity.vo.BaseResponse;

public interface UserService {
    User login(String username);

    BaseResponse regist(RegistUserDTO userDTO);
}
