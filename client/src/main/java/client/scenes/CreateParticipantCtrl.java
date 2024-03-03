package client.scenes;

import client.utils.FileSystemUtils;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.io.IOException;

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

    private FileSystemUtils fileSystemUtils;
    private ServerUtils serverUtils;

    @Inject
    public CreateParticipantCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        //this.invitationCode = invitationCode;
        this.fileSystemUtils = new FileSystemUtils();
        this.serverUtils = new ServerUtils();
    }

    public void onCreate() throws IOException, InterruptedException {
        // todo: send create request (PUT) to /events
        System.out.println("ONCREATE");

        if(firstName == null || lastName == null)
        {
            System.err.println("Error. First name and last name are mandatory.");
            return;
        }

        /*
        try {
            serverUtils.createParticipant(invitationCode, "http://localhost:8080");
        }
        catch (IOException | InterruptedException e)
        {
            System.err.println("Error while sending create request to server");
            return;
        }*/

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