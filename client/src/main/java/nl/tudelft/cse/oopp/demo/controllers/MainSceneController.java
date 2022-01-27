package nl.tudelft.cse.oopp.demo.controllers;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import nl.tudelft.cse.oopp.demo.communication.ServerCommunication;

public class MainSceneController {

    public Button button;

    /**
     * Handles clicking the button.
     */
    public void buttonClicked() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Quote for you");
        alert.setHeaderText(null);
        alert.setContentText(ServerCommunication.getQuote());
        alert.showAndWait();
    }
}
