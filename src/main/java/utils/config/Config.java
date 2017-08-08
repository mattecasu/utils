package utils.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import utils.service.MailSender;


@Slf4j
@SpringBootConfiguration
public class Config {

    @Value("${mail.smtp.host:}")
    private String mailHost;

    @Value("${mail.user:}")
    private String mailUser;

    @Value("${mail.pwd:}")
    private String mailPwd;

    @Value("${mail.from:}")
    private String mailFrom;

    @Value("${mail.smtp.port:465}")
    private Integer smtpPort;

    @Bean
    public MailSender mailReporter() {
        return new MailSender(mailHost, mailUser, mailPwd, mailFrom, smtpPort);
    }


}