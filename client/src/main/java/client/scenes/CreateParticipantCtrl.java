package client.scenes;

import client.utils.EventStompSessionHandler;
import client.utils.FileSystemUtils;
import com.google.inject.Inject;
import commons.Event;
import commons.Participant;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.io.IOException;

/***
 * class CreateParticipantController
 */
public class CreateParticipantCtrl {

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
    private EventStompSessionHandler sessionHandler;

    /***
     * constructor with injection
     * @param mainCtrl
     */
    @Inject
    public CreateParticipantCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.fileSystemUtils = new FileSystemUtils();
    }

    /**
     * Setter for event
     * @param event current event
     * @param sessionHandler current session handler with websocket connection
     */
    public void setEvent(Event event, EventStompSessionHandler sessionHandler) {
        this.sessionHandler = sessionHandler;
        this.event = event;
        firstName.clear();
        lastName.clear();
        iban.clear();
        email.clear();
        bic.clear();
    }

    /**
     * When button gets clicked, send POST request to participants
     * @throws IOException when error occurred while sending the request to server
     * @throws InterruptedException when error occurred while sending the request to server
     */
    public void onCreate(){
        System.out.println("ONCREATE");

        String firstNameString = firstName.getText();
        String lastNameString = lastName.getText();
        String ibanString = iban.getText();
        String bicString = bic.getText();
        String emailString = email.getText();

        if(firstNameString.isEmpty() || lastNameString.isEmpty())
        {
            System.err.println("Error. First name and last name are mandatory.");
            return;
        }


        try {
            sessionHandler.sendParticipant(new Participant(event, firstNameString, lastNameString,
                    emailString,ibanString, bicString), "create");
        }
        catch (Exception e)
        {
            System.err.println("Error while sending create request to server");
            return;
        }
        // todo: Change that to event screen when there is one
        mainCtrl.showEventOverview(event);

    }

    /**
     * Button function to abort creating participant
     */
    public void abort() {
        mainCtrl.showEventOverview(event);
    }
}