package com.almousleck.ecombackend.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService{
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Override
    public void sendOtpEmail(String to, String otp) throws MessagingException {
        Context context = new Context();
        context.setVariable("otp", otp);

        String process = templateEngine.process("otp-email", context);
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setSubject("Verify your account");
        helper.setText(process, true);
        helper.setTo(to);

        mailSender.send(message);
    }

    @Override
    public void sendPasswordResetEmail(String to, String token) throws MessagingException {
        Context context = new Context();
        context.setVariable("token", token);

        String process = templateEngine.process("password-reset-email", context);
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

        helper.setSubject("Password Reset Request");
        helper.setText(process, true);
        helper.setTo(to);

        mailSender.send(mimeMessage);
    }
}
