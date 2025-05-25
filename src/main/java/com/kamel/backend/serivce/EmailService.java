package com.kamel.backend.serivce;

import com.kamel.backend.dto.OrderResponseDto;
import com.kamel.backend.model.EmailVerificationToken;
import com.kamel.backend.model.MyUser;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender _emailSender;
    private final Environment _env;

    @Autowired
    public EmailService(JavaMailSender emailSender, Environment env) {
        this._emailSender = emailSender;
        this._env = env;
    }

    public void sendVerificationEmail(MyUser user, EmailVerificationToken token) {
        System.out.println("sending verification email");
        // constructing the url http://localhost:8000/api/auth/verify-email?token=$userId
        String appUrl = _env.getProperty("app.base-url");
        String verificationUrl = appUrl + "/api/auth/verify-email?tokenId=" + token.getTokenId();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(_env.getProperty("spring.mail.username"));
        message.setTo(user.getEmail());
        message.setSubject("Verification Email");
        message.setText("Click the link to verify your email: " + verificationUrl);

        _emailSender.send(message);
        System.out.println("Email :" + message + " sent successfully");
    }

    public void sendOrderConfirmationEmail(OrderResponseDto orderResponseDto, MyUser buyer) {
        System.out.println("sending order confirmation email");

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(_env.getProperty("spring.mail.username"));
        message.setTo(buyer.getEmail());
        message.setSubject("your order has been placed successfully");
        message.setText(orderResponseDto.toString());

        _emailSender.send(message);
        System.out.println("Email :" + message + " sent successfully");
    }
}
