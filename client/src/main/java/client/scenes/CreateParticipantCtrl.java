package client.scenes;

import client.utils.FileSystemUtils;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.io.IOException;

public class CreateParticipantCtrl {

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

    private Event event;
    private MainCtrl mainCtrl;

    private FileSystemUtils fileSystemUtils;
    private ServerUtils serverUtils;

    @Inject
    public CreateParticipantCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.fileSystemUtils = new FileSystemUtils();
        this.serverUtils = new ServerUtils();
    }

    public void setEvent(Event event) {
        this.event = event;
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
            serverUtils.createParticipant(event, firstName, lastName, email, iban, bic, "http://localhost:8080");
        }
        catch (IOException | InterruptedException e)
        {
            System.err.println("Error while sending create request to server");
            return;
        }*/

        mainCtrl.showStartScreen(); // Change that to event screen

    }
}