package zhenghc.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zhenghc.common.resp.BaseResponse;
import zhenghc.entity.User;
import zhenghc.entity.vo.OrderListVo;
import zhenghc.mapper.OrderMapper;

import java.util.List;

@RestController
@RequestMapping("/myOrder")
public class MyOrderController {

    @Autowired
    private OrderMapper orderMapper;

    @PostMapping("/list")
    public BaseResponse list(){

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<OrderListVo> list = orderMapper.selectAllOrderByUserId(user.getId());


        return BaseResponse.success(list);
    }

}
