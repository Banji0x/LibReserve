package dev.banji.LibReserve.service;

import dev.banji.LibReserve.model.dtos.EmailNotificationDto.BulkEmailNotificationDto;
import dev.banji.LibReserve.model.dtos.EmailNotificationDto.SingleEmailNotificationDto;
import dev.banji.LibReserve.model.dtos.NotificationsConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "library.properties.sendNotifications", name = "enabled", havingValue = "true")
public class NotificationService { //TODO validation also has to be done to ensure the place holders match a regex
    private final SimpMessagingTemplate messagingTemplate;
    private final EmailService emailService;
    private final NotificationsConfig notificationsConfig;


    @PreAuthorize("hasAnyAuthority('SCOPE_LIBRARIAN')")
    public void notifyAllUsersViaWeb(String message) { //TODO make sure this current logged-in user is not included
        messagingTemplate.convertAndSend("/notifications", message);
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_LIBRARIAN')")
    public void notifyAllLibrariansViaWeb(String message) {
        messagingTemplate.convertAndSend("/notifications/librarians", message);
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_LIBRARIAN')")
    public void notifyLibrarianViaWeb(String emailAddress, String message) {
        messagingTemplate.convertAndSendToUser(emailAddress, "/notifications/librarians", message);
    }

    public void notifyLibrarian(String emailAddress, String message, String notificationSubject, String notificationBody) {
        if (notificationsConfig.viaWeb()) //notify via web
            notifyLibrarianViaWeb(emailAddress, message);
        if (notificationsConfig.viaMail()) //notify via email
            this.emailService.sendEmailNotification(new SingleEmailNotificationDto(emailAddress, notificationSubject, notificationBody));
    }

    public void notifyAllLibrarians(String webMessage, BulkEmailNotificationDto bulkEmailNotification) {
        if (notificationsConfig.viaWeb()) //notify via web
            notifyAllLibrariansViaWeb(webMessage);
        if (notificationsConfig.viaMail()) //notify via email
            this.emailService.sendEmailNotification(bulkEmailNotification);
    }

    public void notifyAllUsers(BulkEmailNotificationDto bulkEmailNotification, String message) {
        if (notificationsConfig.viaWeb()) //notify via web
            notifyAllUsersViaWeb(message);
        if (notificationsConfig.viaMail()) //notify via email
            this.emailService.sendEmailNotification(bulkEmailNotification);
    }

    @PreAuthorize("hasAuthority('SCOPE_LIBRARIAN')")
    private void notifyStudentViaWeb(String matricNumber, String message) {
        messagingTemplate.convertAndSendToUser(matricNumber, "/notifications/students", message);
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
    public void notifyAllStudents(List<String> emailAddressList, String notificationSubject, String notificationBody) {
        if (notificationsConfig.viaWeb()) //notify via web
            notifyAllStudentsViaWeb(notificationSubject, notificationBody);
        if (notificationsConfig.viaMail()) //notify via email
            this.emailService.sendEmailNotification(new BulkEmailNotificationDto(emailAddressList, notificationSubject, notificationBody));
    }

    @PreAuthorize("hasAuthority('SCOPE_LIBRARIAN')")
    private void notifyAllStudentsViaWeb(String notificationSubject, String notificationBody) {
        messagingTemplate.convertAndSend("/notifications/students", notificationSubject + "\n" + notificationBody);
    }

}
