package client.scenes;

import client.utils.FileSystemUtils;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;

import javax.json.*;
import java.io.IOException;

public class FirstStartupCtrl {

    private final ServerUtils server;
    private final FileSystemUtils fileSystem;
    private final MainCtrl mainCtrl;

    @FXML
    private TextField invitationCode;

    @FXML
    private TextField firstName;

    @FXML
    private TextField lastName;

    @FXML
    private TextField email;

    // I've named the IBAN and BIC fields with lowercase letters due to
    // java naming conventions, not sure if this is the best way though.
    // Leave a comment in the MR and tell me what you think!
    @FXML
    private TextField iban;

    @FXML
    private TextField bic;

    @Inject
    public FirstStartupCtrl(ServerUtils server, FileSystemUtils fileSystem, MainCtrl mainCtrl) {
        this.fileSystem = fileSystem;
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    public void ok() {

        try {
            fileSystem.saveJsonClient(getjsonclient());
        } catch (IOException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }


        try {
            server.sendJsonClient(getjsonclient());
        } catch (IOException | InterruptedException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }

        // TODO: Continue to next stage
        // MainCtrl.nextStage();
    }


    private JsonObject getjsonclient() {
        return Json.createObjectBuilder()
                .add("code", invitationCode.getText())
                .add("first_name", firstName.getText())
                .add("last_name", lastName.getText())
                .add("email", email.getText())
                .add("IBAN", iban.getText())
                .add("BIC", bic.getText())
                .build();
    }

    // TODO: Uncomment this if required and Adam finished his model
//    private Client getClient() {
//        return new Client(inviteCode.getText(),
//                firstName.getText(),
//                lastName.getText()
//                email.getText(),
//                iban.getText(),
//                bic.getText());
//    }

    public void keyPressed(KeyEvent e) {
        switch (e.getCode()) {
            case ENTER -> ok();
            default -> {
            }
        }
    }
}