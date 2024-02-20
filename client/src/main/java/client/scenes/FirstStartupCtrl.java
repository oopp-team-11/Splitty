package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javax.json.*;

public class FirstStartupCtrl {

    private final ServerUtils server;
    //private final FileSystemUtils fileSystem;
    private final MainCtrl mainCtrl;

    @FXML
    private TextField inviteCode;

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
    public FirstStartupCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    public void ok() {
        // TODO: Uncomment when Marios finishes his code for saving client.json
//        try {
//            fileSystem.saveJsonClient(getJsonClient());
//        } catch (WebApplicationException e) {
//            var alert = new Alert(Alert.AlertType.ERROR);
//            alert.initModality(Modality.APPLICATION_MODAL);
//            alert.setContentText(e.getMessage());
//            alert.showAndWait();
//            return;
//        }

        // TODO: Uncomment when Marios finishes his code for sending to the server
//        try {
//            server.sendJsonClient(getJsonClient());
//        } catch (WebApplicationException e) {
//            var alert = new Alert(Alert.AlertType.ERROR);
//            alert.initModality(Modality.APPLICATION_MODAL);
//            alert.setContentText(e.getMessage());
//            alert.showAndWait();
//            return;
//        }

        // TODO: Continue to next stage
        // MainCtrl.nextStage();
    }

    private JsonObject getjsonclient() {
        return Json.createObjectBuilder()
                .add("code", inviteCode.getText())
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