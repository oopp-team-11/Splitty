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
     * @throws IOException if something goes wrong
     * @throws InterruptedException if something goes wrong with the request
     */
    public void onCreate() throws IOException, InterruptedException {
        // todo: send create request (PUT) to /events
        System.out.println("ONCREATE");
        String eventName = newEventName.getText();
        long invitationCode;


        invitationCode = serverUtils.sendCreateRequest(eventName, "http://localhost:8080");
        fileSystemUtils.saveInvitationCodesToConfigFile(invitationCode,
            "config.json");

        serverUtils.sendJoinRequest(invitationCode, "http://localhost:8080");
        //System.out.println(eventName);


    }

    /**
     * Method that is called when the join button is clicked
     * @throws IOException if something goes wrong
     * @throws InterruptedException if something goes wrong with the request
     */
    public void onJoin() throws IOException, InterruptedException {
        // todo: send get request to /events with invitation code
        // if status == 200
        //      event = response body
        //      MainCtrl.showEventScreen(event)
        // else
        //      MainCtrl.showUserCreationScreen(invitationCode)
        System.out.println("ONJOIN");
        long invitationCode = Integer.parseInt(joinInvitationCode.getText());

        fileSystemUtils.saveInvitationCodesToConfigFile(invitationCode,
            "config.json");
        serverUtils.sendJoinRequest(invitationCode, "http://localhost:8080");
    }
}