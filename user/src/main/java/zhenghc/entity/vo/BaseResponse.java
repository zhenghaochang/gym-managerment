package zhenghc.entity.vo;


import java.io.Serializable;


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

    public static <T> BaseResponse<T> success (T  result) {
        BaseResponse<T> response = new BaseResponse<>();
        response.resCode = "00";
        response.resMsg = "操作成功";
        response.result = result;
        return response;
    }

    public static <T> BaseResponse<T> success (String resMsg ,T  result) {
        BaseResponse<T> response = new BaseResponse<>();
        response.resCode = "00";
        response.resMsg = resMsg;
        response.result = result;
        return response;
    }

    public static <T> BaseResponse<T> error () {
        BaseResponse<T> response = new BaseResponse<>();
        response.resCode = "99";
        response.resMsg = "操作失败";
        return response;
    }

    public static <T> BaseResponse<T> error (String resMsg) {
        BaseResponse<T> response = new BaseResponse<>();
        response.resCode = "99";
        response.resMsg = resMsg;
        return response;
    }

    public String getResCode() {
        return resCode;
    }

    public void setResCode(String resCode) {
        this.resCode = resCode;
    }

    public String getResMsg() {
        return resMsg;
    }

    public void setResMsg(String resMsg) {
        this.resMsg = resMsg;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
