package dev.banji.LibReserve.model.dtos;

import java.util.List;

public record ManagementService(Boolean notifystudents, List<NotificationTimeDto> managementservicenotificationlist) {
}
