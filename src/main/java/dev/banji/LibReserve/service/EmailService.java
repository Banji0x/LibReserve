package dev.banji.LibReserve.service;

import dev.banji.LibReserve.config.conditions.EmailServiceCondition;
import dev.banji.LibReserve.model.dtos.EmailMessageDto.BulkEmailMessageDto;
import dev.banji.LibReserve.model.dtos.EmailMessageDto.SingleEmailMessageDto;
import dev.banji.LibReserve.model.dtos.EmailNotificationDto.BulkEmailNotificationDto;
import dev.banji.LibReserve.model.dtos.EmailNotificationDto.SingleEmailNotificationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Conditional;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@RequiredArgsConstructor
@Conditional(EmailServiceCondition.class)
public class EmailService {
    private final JavaMailSender javaMailSender;

    public void sendEmailMessage(@Validated SingleEmailMessageDto singleEmailMessage) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(singleEmailMessage.getTo().get(0));
        message.setSubject(singleEmailMessage.getSubject());
        message.setText(singleEmailMessage.getBody());
        javaMailSender.send(message);
    }

    public void sendEmailMessage(@Validated BulkEmailMessageDto bulkEmail) {
        bulkEmail.getTo().forEach(address -> {
            sendEmailMessage(new SingleEmailMessageDto(address, bulkEmail.getSubject(), bulkEmail.getBody()));
        });
    }

    public void sendEmailNotification(SingleEmailNotificationDto singleEmailNotification) {
        sendEmailMessage(new SingleEmailMessageDto(singleEmailNotification.getTo().get(0), singleEmailNotification.getSubject(), singleEmailNotification.getBody()));
    }

    public void sendEmailNotification(BulkEmailNotificationDto bulkEmailNotification) {
        sendEmailMessage(new BulkEmailMessageDto(bulkEmailNotification.getTo(), bulkEmailNotification.getSubject(), bulkEmailNotification.getBody()));
    }


}