package dev.banji.LibReserve.service;

import dev.banji.LibReserve.model.dtos.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
//@Conditional(NotificationCondition.class)
public class NotificationService { //TODO validation also has to be done to ensure the place holders match a regex
    private final SimpMessagingTemplate messagingTemplate;

    @PreAuthorize("hasAuthority('SCOPE_LIBRARIAN')")
    public void sendToAllLoggedInUsers(@Payload Message message) { //TODO make sure this current logged in user is not included
        messagingTemplate.convertAndSend("/notifications", message.message());
    }

    @PreAuthorize("hasAuthority('SCOPE_LIBRARIAN')")
    public void sendToAllLibrarians(@Payload Message message) {
        messagingTemplate.convertAndSend("/notifications/librarians", message.message());
    }

    @PreAuthorize("hasAuthority('SCOPE_LIBRARIAN')")
    public void sendToLibrarian(@Payload Message message) {
        messagingTemplate.convertAndSendToUser(message.identifier(), "/notifications/librarians", message.message());
    }

    @PreAuthorize("hasAuthority('SCOPE_LIBRARIAN')")
    public void sendToStudent(@Payload Message message) {
        messagingTemplate.convertAndSendToUser(message.identifier(), "/notifications/students", message.message());
    }

    @PreAuthorize("hasAuthority('SCOPE_LIBRARIAN')")
    public void sendToAllStudents(@Payload Message message) {
        messagingTemplate.convertAndSend("/notifications/students", message.message());
    }

}
