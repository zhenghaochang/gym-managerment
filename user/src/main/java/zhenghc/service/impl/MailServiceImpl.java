package zhenghc.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import zhenghc.common.BaseConstants;
import zhenghc.service.MailService;

import javax.mail.internet.MimeMessage;

@Service
public class MailServiceImpl implements MailService {

    private static final Logger log = LoggerFactory.getLogger(MailServiceImpl.class);
    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.email.from}")
    private String from;

    @Value("${app.email.company}")
    private String company;

    @Autowired
    private TemplateEngine templateEngine;

    /**
     * 使用模板生成邮件内容
     */
    public String generateVerificationEmail(String code, String type) {
        Context context = new Context();

        // 设置模板变量
        context.setVariable("companyName", company);
        context.setVariable("code", code);
        context.setVariable("operation", type);
        context.setVariable("primaryColor", "#4CAF50"); // 主题色

        // 处理模板
        return templateEngine.process("verification-code", context);
    }

    @Override
    public void sendRegistMail(String to, String subject, String code) {

        try{
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(from, company);// 发件人
            helper.setTo(to);// 收件人
            helper.setSubject(subject);// 主题

            String text = generateVerificationEmail(code, BaseConstants.mailType.REGIST);
            helper.setText(text, true);// 内容

            mailSender.send(message);

        } catch (Exception e) {
            log.error("发送邮件失败：{}", e.getMessage());
            throw new RuntimeException(e);
        }


    }






}
