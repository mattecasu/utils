package utils.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;


@Slf4j
public class MailSender {

    private final String host;
    private final String mailUser;
    private final String mailPwd;
    private final String from;
    private final Integer smtpPort;

    public MailSender(String host, String mailUser, String mailPwd, String from, Integer smtpPort) {
        this.host = host;
        this.mailUser = mailUser;
        this.mailPwd = mailPwd;
        this.from = from;
        this.smtpPort = smtpPort;
    }


    public void sendMails(List<String> mailTos, String subject, String body) {

        List<String> tos = mailTos.stream()
                .filter(StringUtils::isNotBlank)
                .collect(toList());

        CompletableFuture.supplyAsync(() ->
                sendMail(tos, subject, body.toString())
        );

    }

    private Integer sendMail(List<String> tos, String subject, String body) {

        tos.forEach(to -> {

                    try {

                        Email email = new SimpleEmail()
                                .setSubject(subject)
                                .setMsg(body)
                                .addTo(to);

                        email.setHostName(host);
                        email.setSmtpPort(smtpPort);

                        if (isNotBlank(mailUser) && isNotBlank(mailPwd)) {
                            email.setSSLOnConnect(true)
                                    .setAuthenticator(new DefaultAuthenticator(mailUser, mailPwd));
                        } else {
                            email.setSSLOnConnect(false);
                        }

                        if (isNotBlank(from))
                            email.setFrom(from);

                        email.send();

                        log.info("Email sent to " + to);

                    } catch (EmailException e) {
                        log.error(e.getMessage());
                    }
                }
        );

        return 1;

    }

}
