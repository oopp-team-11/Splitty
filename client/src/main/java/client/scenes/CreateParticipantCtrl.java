package client.scenes;

import client.utils.FileSystemUtils;
import client.utils.ServerUtils;
import client.utils.TranslationSupplier;
import com.google.inject.Inject;
import commons.Event;
import commons.Participant;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/***
 * class CreateParticipantController
 */
public class CreateParticipantCtrl {

    @FXML
    public Label addParticipantLabel;

    @FXML
    public Button createBtn;

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
     * Sets the translation supplier for this controller
     * @param tl the translation supplier that should be used
     */
    public void setTranslationSupplier(TranslationSupplier tl) {
        this.translationSupplier = tl;
        this.translate();
    }

    private void translate() {
        if (this.translationSupplier == null) return;
        Map<Control, String> labels = new HashMap<>();
        labels.put(this.email, "Email");
        labels.put(this.firstName, "FirstName");
        labels.put(this.lastName, "LastName");
        labels.put(this.addParticipantLabel, "AddAParticipant");
        labels.put(this.createBtn, "Create");
        labels.forEach((key, val) -> {
            var translation = this.translationSupplier.getTranslation(val);
            if (translation == null) return;
            if (key instanceof Labeled)
                ((Labeled) key).setText(translation.replaceAll("\"", ""));
            if (key instanceof TextField)
                ((TextField) key).setPromptText(translation.replaceAll("\"", ""));
        });
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


        mainCtrl.getSessionHandler().sendParticipant(
                new Participant(mainCtrl.getDataHandler().getEvent(),
                        firstNameString,
                        lastNameString,
                        emailString,
                        ibanString,
                        bicString
                ),
                "create"
        );

        mainCtrl.showEventOverview(mainCtrl.getDataHandler().getEvent());

    }

    /**
     * Method for aborting creating participant
     */
    public void abort() {
        mainCtrl.showEventOverview(mainCtrl.getDataHandler().getEvent());
    }
}