package dev.banji.LibReserve.model.dtos;

import java.util.List;

/**
 * To be used to send out messages via email...
 */
public abstract class EmailMessageDto extends EmailDto {
    public EmailMessageDto(String to, String subject, String body) {
        super(to, subject, body);
    }

    public EmailMessageDto(List<String> to, String subject, String body) {
        super(to, subject, body);
    }

    public static class SingleEmailMessageDto extends EmailDto {

        public SingleEmailMessageDto(String to, String subject, String body) {
            super(to, subject, body);
        }
    }


    public static class BulkEmailMessageDto extends EmailDto {
        public BulkEmailMessageDto(List<String> to, String subject, String body) {
            super(to, subject, body);
        }
    }
}
