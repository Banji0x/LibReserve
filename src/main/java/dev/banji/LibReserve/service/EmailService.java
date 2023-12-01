package dev.banji.LibReserve.service;

import dev.banji.LibReserve.model.dtos.EmailDto;
import dev.banji.LibReserve.model.dtos.EmailDto.BulkEmailDto;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "library.properties.sendEmails", havingValue = "true")
public class EmailService {
    private final JavaMailSender javaMailSender;

    public void sendEmail(@Validated EmailDto emailDto) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailDto.to());
        message.setSubject(emailDto.subject());
        message.setText(emailDto.body());

        javaMailSender.send(message);
    }

    public void sendBulkEmail(@Validated BulkEmailDto bulkEmailDto) {
        bulkEmailDto.to().forEach(address -> {
            sendEmail(new EmailDto(address, bulkEmailDto.subject(), bulkEmailDto.body()));
        });
    }
}