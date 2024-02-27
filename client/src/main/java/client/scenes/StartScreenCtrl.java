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

    @Inject
    public StartScreenCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
    }

    public void onCreate() {
        // todo: send create request (PUT) to /events
        System.out.println("ONCREATE");


    }

    public void onJoin() throws IOException, InterruptedException {
        // todo: send get request to /events with invitation code
        // if status == 200
        //      event = response body
        //      MainCtrl.showEventScreen(event)
        // else
        //      MainCtrl.showUserCreationScreen(invitationCode)
        System.out.println("ONJOIN");
        String invitationCode = joinInvitationCode.getText();

        FileSystemUtils.saveInvitationCodesToConfigFile(invitationCode,
            "config.json");
        ServerUtils.sendJoinRequest(invitationCode, "https://test.requestcatcher.com");

        //System.out.println(FileSystemUtils.readInvitationCodes("config.json"));
    }
}