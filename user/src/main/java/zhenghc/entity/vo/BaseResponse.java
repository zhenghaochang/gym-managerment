package zhenghc.entity.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class BaseResponse<T> implements Serializable {

    /**
     * 00-成功 99-失败
     * */
    private String resCode;

    /**
     * 返回信息
     * */
    private String resMsg;

    /**
     * 返回数据
     * */
    private T result;

    public BaseResponse<T> success (T  result) {
        BaseResponse<T> response = new BaseResponse<>();
        response.resCode = "00";
        response.resMsg = "操作成功";
        response.result = result;
        return response;
    }

    public BaseResponse<T> success (String resMsg ,T  result) {
        BaseResponse<T> response = new BaseResponse<>();
        response.resCode = "00";
        response.resMsg = resMsg;
        response.result = result;
        return response;
    }

    public BaseResponse<T> error () {
        BaseResponse<T> response = new BaseResponse<>();
        response.resCode = "99";
        response.resMsg = "操作失败";
        return response;
    }

    public BaseResponse<T> error (String resMsg) {
        BaseResponse<T> response = new BaseResponse<>();
        response.resCode = "99";
        response.resMsg = resMsg;
        return response;
    }


}
