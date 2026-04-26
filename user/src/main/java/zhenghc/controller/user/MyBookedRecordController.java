package zhenghc.controller.user;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zhenghc.common.resp.BaseResponse;
import zhenghc.entity.BookingPrivateRecord;
import zhenghc.entity.BookingRecord;
import zhenghc.entity.User;
import zhenghc.mapper.BookingPrivateRecordMapper;
import zhenghc.mapper.BookingRecordMapper;

import java.util.List;

@RestController
@RequestMapping("/bookedRecord")
public class MyBookedRecordController {

    private final BookingRecordMapper bookingRecordMapper;

    private final BookingPrivateRecordMapper bookingPrivateRecordMapper;

    public MyBookedRecordController(BookingRecordMapper bookingRecordMapper, BookingPrivateRecordMapper bookingPrivateRecordMapper) {
        this.bookingRecordMapper = bookingRecordMapper;
        this.bookingPrivateRecordMapper = bookingPrivateRecordMapper;
    }

    @PostMapping("/list")
    public BaseResponse list(){

        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<BookingPrivateRecord> list = bookingPrivateRecordMapper.selectAllByUserId(user.getId());
        return BaseResponse.success(list);
    }

    @PostMapping("/delete")
    public BaseResponse delete(@RequestBody Long id){

        bookingRecordMapper.deleteById(id);

        return BaseResponse.success("删除成功", null);
    }

}
