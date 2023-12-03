package dev.banji.LibReserve.config.conditions;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.stereotype.Component;

@Component
public class EmailServiceCondition implements Condition {
    @Value("library.properties.sendNotifications.viaMail")
    private Boolean sendNotificationsViaMail;
    @Value("library.properties.sendMessagesViaEmail")
    private Boolean sendMessagesViaMail;

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return sendMessagesViaMail || sendNotificationsViaMail;
    }
}
