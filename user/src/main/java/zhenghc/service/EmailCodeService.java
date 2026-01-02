package zhenghc.service;

public interface EmailCodeService {

    void createCode(String email);

    void checkCode(String email, String code);

}
