package server;

import org.springframework.stereotype.Service;

/**
 * Password Service generating and outputting a new random password on server startup
 */
@Service
public class PasswordService {
    /**
     * Getter for admin password
     *
     * @return returns the admin password as string
     */
    public String getAdminPassword() {
        //TODO: Replace that with a random password generation on runtime (can be a random UUID I guess)
        return "secretPasscode";
    }
}
