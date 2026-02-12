package zhenghc.service.user;

import zhenghc.entity.User;
import zhenghc.entity.dto.RegistUserDTO;
import zhenghc.common.resp.BaseResponse;

public interface UserService {
    User login(String username);

    BaseResponse regist(RegistUserDTO userDTO);
}
