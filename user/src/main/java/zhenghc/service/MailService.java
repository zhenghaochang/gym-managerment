package zhenghc.service;

public interface MailService {

    void sendRegistMail(String to, String subject, String code);

}
