package server;

import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;

/**
 * Custom ChannelInterceptor for Admin topic subscription validation
 */
public class AdminValidation implements ChannelInterceptor {
    private final PasswordService passwordService;

    /**
     * Custom constructor that accepts a password service
     * @param passwordService password service
     */
    public AdminValidation(PasswordService passwordService) {
        this.passwordService = passwordService;
    }
    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);
        if (StompCommand.SUBSCRIBE.equals(headerAccessor.getCommand())
                && headerAccessor.getDestination().startsWith("/topic/admin")
                && !passwordService.getAdminPassword()
                .equals(headerAccessor.getFirstNativeHeader("passcode"))) {
            throw new
                    MessagingException("This user tried to subscribe to an admin channel with an incorrect password.");
        }
        return message;
    }
}
