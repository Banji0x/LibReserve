package dev.banji.LibReserve.model.dtos;

public record NotificationsConfig(Boolean enabled, Boolean viaWeb,
                                  Boolean viaMail, String timeUpNotificationSubject,
                                  String timeUpNotificationBody, String blackListNotificationSubject,
                                  String blackListNotificationBody, String timeAlmostUpNotificationSubject,
                                  String timeAlmostUpNotificationBody, String studentBlackListNotificationSubject,
                                  String studentBlackListNotificationBody, String studentKickedOutNotificationSubject,
                                  String studentKickedOutNotificationBody) {
}