package com.hcvision.hcvisionserver.mail;

import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public static final String EMAIL_VERIFICATION_SUBJECT = "Mail Confirmation";
    public static final String RESET_PASSWORD_OTP_SUBJECT = "Reset Password";
    public static final String PASSWORD_CHANGE_NOTIFICATION_SUBJECT = "Password Changed";


    @Async
    public void send(String to, String content, String subject) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(content, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom("auth@hcvision.com");
            mailSender.send(mimeMessage);
        } catch (jakarta.mail.MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private static final String htmlTopPage =
            "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
            "    <title>Email Verification - HCvision</title>\n" +
            "</head>\n" +
            "<body style=\"font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0;\">\n" +
            "    <table width=\"100%\" bgcolor=\"#f4f4f4\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
            "        <tr>\n" +
            "            <td align=\"center\">\n" +
            "                <table width=\"600\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"background-color: #ffffff; margin: 20px 0; padding: 20px; border-radius: 10px; box-shadow: 0 4px 6px rgba(0,0,0,0.1);\">\n" +
            "                    <tr>\n" +
            "                        <td align=\"center\">\n" +
            "                            <img src=\"http://hcvision:8080/api/v1/resources/logo-light\" alt=\"HCvision Logo\" style=\"max-width: 500;\">\n" +
            "                        </td>\n" +
            "                    </tr>\n";

    private static final String htmlBottomPage =
            "                       <tr>\n" +
            "                        <td style=\"background-color: #0070C0; padding: 10px 0; text-align: center;\">\n" +
            "                            <p style=\"color: #fff; margin: 0;\">© 2023 HCvision. All rights reserved.</p>\n" +
            "                        </td>\n" +
            "                    </tr>\n" +
            "                </table>\n" +
            "            </td>\n" +
            "        </tr>\n" +
            "    </table>\n" +
            "</body>\n" +
            "</html>";


    public String buildVerificationEmail(String name, String link) {
        return  htmlTopPage +
                "                    <tr>\n" +
                "                        <td style=\"padding: 20px;\">\n" +
                "                            <h2 style=\"color: #333;\">Hello, " + name + "</h2>\n" +
                "                            <p style=\"color: #555; line-height: 1.6;\">Thank you for joining HCvision, the application for visualizing hierarchical clustering. To complete your registration, please click the button below to verify your email address.</p>\n" +
                "                            <a href=\"" + link + "\" style=\"display: inline-block; background-color: #0070C0; color: #fff; padding: 10px 20px; text-decoration: none; border-radius: 5px; margin-top: 20px;\">Verify Email</a>\n" +
                "                            <p style=\"color: #555; line-height: 1.6; margin-top: 20px;\">If the button above doesn't work, you can also copy and paste the following link into your browser:</p>\n" +
                "                            <p style=\"color: #555; line-height: 1.6;\">" + link + "</p>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                htmlBottomPage;

    }

    public String buildPasswordChangedEmail(String name) {
        return  htmlTopPage +
                "                    <tr>\n" +
                "                        <td style=\"padding: 20px;\">\n" +
                "                            <h2 style=\"color: #333;\">Hello, " + name + "</h2>\n" +
                "                            <p style=\"color: #555; line-height: 1.6;\">Your password has been successfully updated.</p>\n" +
                "                            <p style=\"color: #555; line-height: 1.6;\">Action taken " + LocalDateTime.now() + "</p>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "                    <tr>\n" +
                "                        <td align=\"center\">\n" +
                "                            <img src=\"http://hcvision:8080/api/v1/resources/reset-password\" alt=\"HCvision Logo\" style=\"max-width: 300;\">\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                htmlBottomPage;
    }

    public String buildOtpEmail(String name, String otp) {
        return  htmlTopPage +
                "                    <tr>\n" +
                "                        <td style=\"padding: 20px;\">\n" +
                "                            <h2 style=\"color: #333;\">Hello, " + name + "</h2>\n" +
                "                            <p style=\"color: #555; line-height: 1.6;\">Use this one time code to set your password:" +
                "                                <span style=\"display: inline-block; background-color: #0070C0; color: #fff; padding: 10px 20px; text-decoration: none; border-radius: 5px; margin-top: 20px;\">" + otp +
                "                            </p>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "                    <tr>\n" +
                "                        <td style=\"padding: 20px;\">\n" +
                "                            <p style=\"color: #555; line-height: 1.6;\">Action taken at " + LocalDateTime.now() +
                "                        </td>\n" +
                "                    </tr>\n" +
                htmlBottomPage;
    }
}
