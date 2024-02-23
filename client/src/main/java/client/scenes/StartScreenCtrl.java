package client.scenes;

import client.utils.FileSystemUtils;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;

import javax.json.*;
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

    public void onJoin() {
        // todo: send get request to /events with invitation code
        // if status == 200
        //      event = response body
        //      MainCtrl.showEventScreen(event)
        // else
        //      MainCtrl.showUserCreationScreen(invitationCode)
        System.out.println("ONJOIN");
    }
}