package dev.banji.LibReserve.service;

import dev.banji.LibReserve.config.properties.LibraryConfigurationProperties;
import dev.banji.LibReserve.exceptions.DisabledNotificationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final LibraryConfigurationProperties libraryConfigurationProperties;

    public Boolean sendNotifications(int timeLeft, boolean immediately) {
// Send notifications out. {probably to the Librarian and Student...} //TODO notifications Service
        if (!libraryConfigurationProperties.getSendNotifications())
            throw new DisabledNotificationException();
        //alert users
        if (immediately) {

        }
        List<Integer> notificationRolloutTimeList = libraryConfigurationProperties.getNotificationTimeListInMinutes();
        List<Integer> list = notificationRolloutTimeList.stream().filter(notificationTime -> notificationTime.equals(timeLeft)).toList();
//        if (list.isEmpty()) return;

        //send out notifications
        //write logic here #TODO
        return false;
    }
}
