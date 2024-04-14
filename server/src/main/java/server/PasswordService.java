package server;

import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Password Service generating and outputting a new random password on server startup
 */
@Service
public class PasswordService {

    private String adminPassword;

    /**
     * Generates a new password on new instance creation
     */
    public PasswordService() {
        adminPassword = UUID.randomUUID().toString();
        printBanner(adminPassword);
    }

    /**
     * Getter for admin password
     *
     * @return returns the admin password as string
     */
    public String getAdminPassword() {
        return adminPassword;
    }

    /**
     * Prints a banner with the given password
     *
     * @param password the password to be included in the banner
     */
    public void printBanner(String password) {
        String banner = "*".repeat(password.length() + 30);
        System.out.println(banner);
        System.out.println("* Generated admin password: " + password + " *");
        System.out.println(banner);
    }
}
