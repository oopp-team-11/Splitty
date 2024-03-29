package server;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuration class for websocket. Defines prefixes for client-server websocket communication
 * and websocket endpoints.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final PasswordService passwordService;

    /**
     * Constructor accepting passwordService
     *
     * @param passwordService passwordService
     */
    public WebSocketConfig(PasswordService passwordService) {
        this.passwordService = passwordService;
    }

    /**
     * Configures prefixes for client-server communication
     * @param config websocket config
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
    }

    /**
     * Adds the AdminValidation channel interceptor
     * @param registration registration for channel interceptors
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new AdminValidation(passwordService));
    }

    /**
     * Configures endpoints for websocket
     * @param registry registry of websocket endpoints
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/v1");
    }
}