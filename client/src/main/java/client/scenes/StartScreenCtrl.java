package client.scenes;

import client.utils.FileSystemUtils;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.io.IOException;

public class StartScreenCtrl {
    private final MainCtrl mainCtrl;

    @FXML
    private TextField newEventName;

    @FXML
    private TextField joinInvitationCode;

    private FileSystemUtils fileSystemUtils;
    private ServerUtils serverUtils;

    @Inject
    public StartScreenCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        fileSystemUtils = new FileSystemUtils();
        serverUtils = new ServerUtils();
    }

    /**
     * Method that is called when the create button is clicked
     */
    public void onCreate() {
        // todo: send create request (PUT) to /events
        System.out.println("ONCREATE");
        String eventName = newEventName.getText();
        long invitationCode;

        try {
            invitationCode = serverUtils.createEvent(eventName, "http://localhost:8080");
        }
        catch (IOException | InterruptedException e) {
            System.err.println("Error while sending create request to server");
            return;
        }

        try {
            fileSystemUtils.saveInvitationCodesToConfigFile(invitationCode,
                "config.json");
        }
        catch (IOException e) {
            System.err.println("Error while saving invitation code to config file");
        }

        try {
            serverUtils.getEvent(invitationCode, "http://localhost:8080");
        }
        catch (IOException | InterruptedException e) {
            System.err.println("Error while sending get request to server");
        }
    }

    /**
     * Method that is called when the join button is clicked
     */
    public void onJoin() {
        // todo: send get request to /events with invitation code
        // if status == 200
        //      event = response body
        //      MainCtrl.showEventScreen(event)
        // else
        //      MainCtrl.showUserCreationScreen(invitationCode)
        System.out.println("ONJOIN");
        long invitationCode = Integer.parseInt(joinInvitationCode.getText());

        try{
            fileSystemUtils.saveInvitationCodesToConfigFile(invitationCode,
                "config.json");
        }
        catch (IOException e) {
            System.err.println("Error while saving invitation code to config file");
        }

        try {
            serverUtils.getEvent(invitationCode, "http://localhost:8080");
        }
        catch (IOException | InterruptedException e) {
            System.err.println("Error while sending get request to server");
        }
    }
}