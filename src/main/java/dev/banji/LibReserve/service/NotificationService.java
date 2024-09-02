package dev.banji.LibReserve.service;

import dev.banji.LibReserve.config.properties.LibraryConfigurationProperties;
import dev.banji.LibReserve.model.dtos.EmailNotificationDto.BulkEmailNotificationDto;
import dev.banji.LibReserve.model.dtos.EmailNotificationDto.SingleEmailNotificationDto;
import dev.banji.LibReserve.model.dtos.NotificationsConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@ConditionalOnProperty(prefix = "library.properties", name = "enablenotificationservice", havingValue = "true")
public class NotificationService { //TODO validation also has to be done to ensure the place holders match a regex
    private SimpMessagingTemplate messagingTemplate;
    private EmailService emailService;
    private NotificationsConfig notificationsConfig;

    @Autowired
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    @Autowired
    private void setNotificationsConfig(LibraryConfigurationProperties libraryConfigurationProperties) {
        this.notificationsConfig = libraryConfigurationProperties.getSendStudentNotifications();
    }

    @Autowired
    private void setMessagingTemplate(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

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


    @PreAuthorize("hasAuthority('SCOPE_LIBRARIAN')")
    private void notifyStudentViaWeb(String matricNumber, String message) {
        messagingTemplate.convertAndSendToUser(matricNumber, "/notifications/students", message);
    }

    @PreAuthorize("hasAuthority('SCOPE_LIBRARIAN')")
    //TODO validate the parameters...
    public void timeUpNotification(String emailAddress) {
        sendNotification(emailAddress, notificationsConfig.timeUpNotificationSubject(), notificationsConfig.timeUpNotificationBody());
    }

    @PreAuthorize("hasAuthority('SCOPE_LIBRARIAN')")
    //TODO validate the parameters...
    public void timeAlmostNotification(String emailAddress, long remainingTimeInMinutes) {
        sendNotification(emailAddress, notificationsConfig.timeUpNotificationSubject(), remainingTimeInMinutes + " " + notificationsConfig.timeUpNotificationBody());
    }

    @PreAuthorize("hasAuthority('SCOPE_LIBRARIAN')")
    //TODO validate the parameters...
    public void blackListNotification(String emailAddress) {
        sendNotification(emailAddress, notificationsConfig.blackListNotificationSubject(), notificationsConfig.blackListNotificationBody());
    }

    @PreAuthorize("hasAuthority('SCOPE_LIBRARIAN')")
    //TODO validate the parameters...
    public void studentKickedOutNotification(String emailAddress) {
        sendNotification(emailAddress, notificationsConfig.blackListNotificationSubject(), notificationsConfig.blackListNotificationBody());
    }

    @PreAuthorize("hasAuthority('SCOPE_LIBRARIAN')")
    //TODO validate the parameters...
    public void studentBlackListNotification(String emailAddress) {
        sendNotification(emailAddress, notificationsConfig.studentBlackListNotificationSubject(), notificationsConfig.studentBlackListNotificationBody());
    }

    @PreAuthorize("hasAuthority('SCOPE_LIBRARIAN')")
    //TODO validate the parameters...
    private void sendNotification(String emailAddress, String notificationSubject, String notificationBody) {
        if (notificationsConfig.viaWeb()) //notify via web
            notifyStudentViaWeb(emailAddress, notificationSubject + "\n" + notificationBody);
        if (emailService != null) //notify via email
            this.emailService.sendEmailNotification(new SingleEmailNotificationDto(emailAddress, notificationSubject, notificationBody));
    }

    @PreAuthorize("hasAuthority('SCOPE_LIBRARIAN')")
    //TODO validate the parameters...
    public void notifyAllStudents(List<String> emailAddressList, String notificationSubject, String notificationBody) {
        if (notificationsConfig.viaWeb()) //notify via web
            notifyAllStudentsViaWeb(notificationSubject, notificationBody);
        if (emailService != null) //notify via email
            this.emailService.sendEmailNotification(new BulkEmailNotificationDto(emailAddressList, notificationSubject, notificationBody));
    }

    @PreAuthorize("hasAuthority('SCOPE_LIBRARIAN')")
    private void notifyAllStudentsViaWeb(String notificationSubject, String notificationBody) {
        messagingTemplate.convertAndSend("/notifications/students", notificationSubject + "\n" + notificationBody);
    }

}
