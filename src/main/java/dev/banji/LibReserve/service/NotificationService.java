package dev.banji.LibReserve.service;

import dev.banji.LibReserve.model.dtos.NotificationsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "library.properties.sendNotifications", havingValue = "true")
public class NotificationService { //TODO validation also has to be done to ensure the place holders match a regex
    private final SimpMessagingTemplate messagingTemplate;

    @PreAuthorize("hasAuthority('SCOPE_LIBRARIAN')")
    public void notifyAllUsers(@Payload @Validated NotificationsDto notification) { //TODO make sure this current logged in user is not included
        messagingTemplate.convertAndSend("/notifications", notification.message());
    }

    @PreAuthorize("hasAuthority('SCOPE_LIBRARIAN')")
    public void notifyAllLibrarians(@Payload @Validated NotificationsDto notification) {
        messagingTemplate.convertAndSend("/notifications/librarians", notification.message());
    }

    @PreAuthorize("hasAuthority('SCOPE_LIBRARIAN')")
    public void notifyLibrarian(@Payload @Validated NotificationsDto notification) {
        messagingTemplate.convertAndSendToUser(notification.userIdentifier(), "/notifications/librarians", notification.message());
    }

    @PreAuthorize("hasAuthority('SCOPE_LIBRARIAN')")
    public void notifyStudent(@Payload @Validated NotificationsDto notification) {
        messagingTemplate.convertAndSendToUser(notification.userIdentifier(), "/notifications/students", notification.message());
    }

    @PreAuthorize("hasAuthority('SCOPE_LIBRARIAN')")
    public void notifyAllStudents(@Payload @Validated NotificationsDto notification) {
        messagingTemplate.convertAndSend("/notifications/students", notification.message());
    }

}
