package dev.banji.LibReserve.model.dtos;

import java.util.List;

/**
 * This is a marker class to indicate that this is to be used for notifications purposes...
 */
public abstract class EmailNotificationDto extends EmailDto {
    public EmailNotificationDto(String to, String subject, String body) {
        super(to, subject, body);
    }

    public EmailNotificationDto(List<String> to, String subject, String body) {
        super(to, subject, body);
    }

    /**
     * used to send single notifications via emails...
     */
    public static class SingleEmailNotificationDto extends EmailNotificationDto {

        public SingleEmailNotificationDto(String emailAddress, String subject, String body) {
            super(emailAddress, subject, body);
        }
    }

    /**
     * used to send bulk notifications via emails
     */
    public static class BulkEmailNotificationDto extends EmailNotificationDto {
        public BulkEmailNotificationDto(List<String> emailAddressList, String subject, String body) {
            super(emailAddressList, subject, body);
        }
    }
}
