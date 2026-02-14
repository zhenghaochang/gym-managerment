package zhenghc.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zhenghc.common.resp.BaseResponse;
import zhenghc.entity.Order;
import zhenghc.entity.OrderItem;
import zhenghc.entity.User;
import zhenghc.entity.dto.MemberCourseDTO;
import zhenghc.entity.vo.CourseVo;
import zhenghc.mapper.CourseMapper;
import zhenghc.mapper.MemberCourseMapper;
import zhenghc.mapper.OrderItemMapper;
import zhenghc.mapper.OrderMapper;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/course")
public class CourseController {

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private MemberCourseMapper memberCourseMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @PostMapping("/list")
    public BaseResponse list(){

        List<CourseVo> list = courseMapper.selectAll();

        return BaseResponse.success(list);
    }

    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/buyCourse")
    public BaseResponse buyCourse(@RequestBody MemberCourseDTO param){

        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String orderNum = "SJK" + System.currentTimeMillis();

        Order order = new Order();

        order.setOrderNum(orderNum);
        order.setOrderAmount(param.getOrderAmount());
        order.setPaymentAmount(param.getPaymentAmount());
        order.setPaymentMethod(param.getPaymentMethod());
        order.setPaymentStatus(2);
        order.setOrderStatus(2);
        order.setUserId(user.getId());
        order.setPaymentTime(new Date());

        orderMapper.insertNew(order);

        OrderItem item = new OrderItem();
        item.setOrderId(order.getId());
        item.setProductId(param.getCourseId());
        item.setProductType(2);
        item.setProductName(param.getCourseName());
        item.setQuantity(param.getQuantity());
        item.setProductPrice(param.getProductPrice());
        item.setSubtotal(BigDecimal.valueOf(param.getQuantity()).multiply(param.getProductPrice()));

        orderItemMapper.insert(item);

        param.setOrderNum(orderNum);
        param.setStatus(1);
        memberCourseMapper.insert(param);



        return BaseResponse.success("购买成功",null);
    }

}
