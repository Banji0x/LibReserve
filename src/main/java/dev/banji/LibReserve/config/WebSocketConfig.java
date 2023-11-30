package dev.banji.LibReserve.config;


import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Service
//@Conditional(NotificationCondition.class)
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws");
        registry.addEndpoint("/ws").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/notifications");
        registry.setApplicationDestinationPrefixes("/libReserve/messaging/");
    }
}