package client.scenes;

import client.utils.FileSystemUtils;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Participant;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.io.IOException;

public class EditParticipantCtrl {

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

    private Participant participant;
    private MainCtrl mainCtrl;

    private FileSystemUtils fileSystemUtils;
    private ServerUtils serverUtils;

    @Inject
    public EditParticipantCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.fileSystemUtils = new FileSystemUtils();
        this.serverUtils = new ServerUtils();
    }

    /**
     * Setter for participant
     * @param participant
     */
    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    /**
     * When button gets clicked, send PUT request to participants
     * @throws IOException when error occurred while sending the request to server
     * @throws InterruptedException when error occurred while sending the request to server
     */
    public void onEdit() throws IOException, InterruptedException {
        System.out.println("ONEDIT");

        String firstNameString = firstName.getText();
        String lastNameString = lastName.getText();
        String ibanString = iban.getText();
        String bicString = bic.getText();
        String emailString = email.getText();


        try {
            serverUtils.editParticipant(participant.getId(), firstNameString, lastNameString,
                    emailString, ibanString, bicString, "http://localhost:8080");
        }
        catch (IOException | InterruptedException e)
        {
            System.err.println("Error while sending edit request to server");
            return;
        }

        mainCtrl.showStartScreen(); // todo: Change that to event screen when there is one

    }
}