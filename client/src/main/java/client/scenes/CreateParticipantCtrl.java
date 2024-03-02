package client.scenes;

import client.utils.FileSystemUtils;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class CreateParticipantCtrl {
    private final MainCtrl mainCtrl;

    @FXML
    private TextField id;

    @FXML
    private TextField email;

    @FXML
    private TextField firstName;

    @FXML
    private TextField lastName;

    @FXML
    private TextField iban;

    @FXML
    private TextField bic;

    private long invitationCode;

    private FileSystemUtils fileSystemUtils;
    private ServerUtils serverUtils;

    @Inject
    public CreateParticipantCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        //this.invitationCode = invitationCode;
        this.fileSystemUtils = new FileSystemUtils();
        this.serverUtils = new ServerUtils();
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