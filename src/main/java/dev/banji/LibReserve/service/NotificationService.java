package dev.banji.LibReserve.service;

import dev.banji.LibReserve.model.dtos.EmailNotificationDto.BulkEmailNotificationDto;
import dev.banji.LibReserve.model.dtos.EmailNotificationDto.SingleEmailNotificationDto;
import dev.banji.LibReserve.model.dtos.WebNotificationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "library.properties.sendNotifications", name = "enabled", havingValue = "true")
public class NotificationService { //TODO validation also has to be done to ensure the place holders match a regex
    private final SimpMessagingTemplate messagingTemplate;
    private final EmailService emailService;
    //TODO this should be injected from a file...
    private final String notificationSubject;
    //TODO this should be injected from a file...
    private final String notificationBody;
    @Value("library.properties.sendNotifications.viaWeb")
    private Boolean notifyViaWeb;
    @Value("library.properties.sendNotifications.viaMail")
    private Boolean notifyViaMail;

    @PreAuthorize("hasAnyAuthority('SCOPE_LIBRARIAN')")
    public void notifyAllUsersViaWeb(@Payload @Validated WebNotificationDto notification) { //TODO make sure this current logged in user is not included
        messagingTemplate.convertAndSend("/notifications", notification.message());
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_LIBRARIAN')")
    public void notifyAllLibrariansViaWeb(@Payload @Validated WebNotificationDto notification) {
        messagingTemplate.convertAndSend("/notifications/librarians", notification.message());
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_LIBRARIAN')")
    public void notifyLibrarianViaWeb(@Payload @Validated WebNotificationDto notification) {
        messagingTemplate.convertAndSendToUser(notification.userIdentifier().get(0), "/notifications/librarians", notification.message());
    }

    public void notifyLibrarian(WebNotificationDto notificationTimerDto, String emailAddress) {
        if (notifyViaWeb) //notify via web
            notifyLibrarianViaWeb(notificationTimerDto);
        if (notifyViaMail) //notify via email
            this.emailService.sendEmailNotification(new SingleEmailNotificationDto(emailAddress, notificationSubject, notificationBody));
    }

    public void notifyAllLibrarians(WebNotificationDto notificationTimerDto, List<String> emailAddressList) {
        if (notifyViaWeb) //notify via web
            notifyAllLibrariansViaWeb(notificationTimerDto);
        if (notifyViaMail) //notify via email
            this.emailService.sendEmailNotification(new BulkEmailNotificationDto(emailAddressList, notificationSubject, notificationBody));
    }

    public void notifyAllUsers(WebNotificationDto notificationTimerDto, List<String> emailAddressList) {
        if (notifyViaWeb) //notify via web
            notifyAllUsersViaWeb(notificationTimerDto);
        if (notifyViaMail) //notify via email
            this.emailService.sendEmailNotification(new BulkEmailNotificationDto(emailAddressList, notificationSubject, notificationBody));
    }

    @PreAuthorize("hasAuthority('SCOPE_LIBRARIAN')")
    private void notifyStudentViaWeb(@Payload @Validated WebNotificationDto notification) {
        messagingTemplate.convertAndSendToUser(notification.userIdentifier().get(0), "/notifications/students", notification.message());
    }

    @PreAuthorize("hasAuthority('SCOPE_LIBRARIAN')")
    //TODO validate the parameters...
    public void notifyStudent(String emailAddress, WebNotificationDto notificationTimerDto) {
        if (notifyViaWeb) //notify via web
            notifyStudentViaWeb(notificationTimerDto);
        if (notifyViaMail) //notify via email
            this.emailService.sendEmailNotification(new SingleEmailNotificationDto(emailAddress, notificationSubject, notificationBody));
    }

    @PreAuthorize("hasAuthority('SCOPE_LIBRARIAN')")
    //TODO validate the parameters...
    public void notifyAllStudents(WebNotificationDto notificationTimerDto, List<String> emailAddressList) {
        if (notifyViaWeb) //notify via web
            notifyAllStudentsViaWeb(notificationTimerDto);
        if (notifyViaMail) //notify via email
            this.emailService.sendEmailNotification(new BulkEmailNotificationDto(emailAddressList, notificationSubject, notificationBody));
    }

    @PreAuthorize("hasAuthority('SCOPE_LIBRARIAN')")
    private void notifyAllStudentsViaWeb(@Payload @Validated WebNotificationDto notification) {
        messagingTemplate.convertAndSend("/notifications/students", notification.message());
    }

}
