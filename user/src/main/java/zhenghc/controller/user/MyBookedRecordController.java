package zhenghc.controller.user;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zhenghc.common.resp.BaseResponse;
import zhenghc.entity.BookingRecord;
import zhenghc.entity.User;
import zhenghc.mapper.BookingRecordMapper;

import java.util.List;

@RestController
@RequestMapping("/bookedRecord")
public class MyBookedRecordController {

    private final BookingRecordMapper bookingRecordMapper;

    public MyBookedRecordController(BookingRecordMapper bookingRecordMapper) {
        this.bookingRecordMapper = bookingRecordMapper;
    }

    @PostMapping("/list")
    public BaseResponse list(){

        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<BookingRecord> list = bookingRecordMapper.selectAllByUserId(user.getId());
        return BaseResponse.success(list);
    }

    @PostMapping("/delete")
    public BaseResponse delete(@RequestBody Long id){

        bookingRecordMapper.deleteById(id);

        return BaseResponse.success("删除成功", null);
    }

}
