package client.scenes;

import client.utils.FileSystemUtils;
import client.utils.ServerUtils;
import client.utils.TranslationSupplier;
import com.google.inject.Inject;
import commons.Event;
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
    private ServerUtils serverUtils;
    private TranslationSupplier translationSupplier;

    /***
     * constructor with injection
     * @param mainCtrl
     */
    @Inject
    public CreateParticipantCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.fileSystemUtils = new FileSystemUtils();
        this.serverUtils = new ServerUtils();
    }

    /**
     * Setter for event
     * @param event
     */
    public void setEvent(Event event) {
        this.event = event;
    }

    /**
     * When button gets clicked, send POST request to participants
     * @throws IOException when error occurred while sending the request to server
     * @throws InterruptedException when error occurred while sending the request to server
     */
    public void onCreate() throws IOException, InterruptedException {
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
            serverUtils.createParticipant(event.getId(), firstNameString, lastNameString, emailString,
                    ibanString, bicString, "http://localhost:8080");
        }
        catch (IOException | InterruptedException e)
        {
            System.err.println("Error while sending create request to server");
            return;
        }

        mainCtrl.showStartScreen(); // todo: Change that to event screen when there is one

    }

    public void setTranslationSupplier(TranslationSupplier tl) {
        this.translationSupplier = tl;
    }
}