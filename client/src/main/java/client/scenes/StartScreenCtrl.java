package client.scenes;

import client.utils.FileSystemUtils;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Modality;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class StartScreenCtrl implements Initializable {
    private final MainCtrl mainCtrl;

    @FXML
    private TextField newEventName;

    @FXML
    private TextField joinInvitationCode;

    private ObservableList<Event> data;

    @FXML
    private TableView<Event> eventTable;

    @FXML
    private TableColumn<Event, String> eventNameColumn;

    @FXML
    private TableColumn<Event, String> invitationCodeColumn;

    private final FileSystemUtils fileSystemUtils;
    private final ServerUtils serverUtils;

    @Inject
    public StartScreenCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        fileSystemUtils = new FileSystemUtils();
        serverUtils = new ServerUtils();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        eventNameColumn.setCellValueFactory(q -> new SimpleStringProperty(q.getValue().getTitle()));
        invitationCodeColumn.setCellValueFactory(q -> new SimpleStringProperty(q.getValue().getId().toString()));
        eventTable.setRowFactory(tv -> {
            TableRow<Event> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Event rowData = row.getItem();
                    joinInvitationCode.setText(rowData.getId().toString());
                    onJoin();
                }
            });
            return row;
        });
    }

    /**
     * Refreshes the events table.
     */
    public void refresh() throws IOException, InterruptedException {
        var events = serverUtils.getRecentEvents("https://127.0.0.1:8080", "config.json");
        data = FXCollections.observableList(events);
        eventTable.setItems(data);
    }

    /**
     * Method that is called when the create button is clicked
     */
    public void onCreate() {
        System.out.println("ONCREATE");
        String eventName = newEventName.getText();

        // TODO: validate eventName first
        long invitationCode;
        try {
            invitationCode = serverUtils.createEvent(eventName, "http://localhost:8080");
        } catch (IOException | InterruptedException e) {
            serverErrorAlert(e);
            return;
        }

        try {
            fileSystemUtils.saveInvitationCodesToConfigFile(invitationCode,
                    "config.json");
        } catch (IOException e) {
            fileSaveErrorAlert(e);
            return;
        }

        try {
            serverUtils.getEvent(invitationCode, "http://localhost:8080");
        } catch (IOException | InterruptedException e) {
            serverErrorAlert(e);
        }
    }

    /**
     * Method that is called when the join button is clicked
     */
    public void onJoin() {
        // todo: send get request to /events with invitation code
        // if status == 200
        //      event = response body
        //      MainCtrl.showEventScreen(event)
        // else
        //      MainCtrl.showUserCreationScreen(invitationCode)
        System.out.println("ONJOIN: " + joinInvitationCode.getText());
        long invitationCode;
        try {
            invitationCode = Integer.parseInt(joinInvitationCode.getText());
        } catch (NumberFormatException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText("Invalid invitation code, try again." +
                    "\nError: " + (e.getMessage() != null ? e.getMessage() : "No error message available."));
            alert.showAndWait();
            return;
        }

        try {
            fileSystemUtils.saveInvitationCodesToConfigFile(invitationCode,
                    "config.json");
        } catch (IOException e) {
            fileSaveErrorAlert(e);
            return;
        }

        try {
            serverUtils.getEvent(invitationCode, "http://localhost:8080");
        } catch (IOException | InterruptedException e) {
            serverErrorAlert(e);
        }
    }

    private static void serverErrorAlert(Exception e) {
        var alert = new Alert(Alert.AlertType.ERROR);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setContentText("Something went wrong while trying to connect to server." +
                "\nError: " + (e.getMessage() != null ? e.getMessage() : "No error message available."));
        alert.showAndWait();
    }

    private static void fileSaveErrorAlert(Exception e) {
        var alert = new Alert(Alert.AlertType.ERROR);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setContentText("Something went wrong while saving invitation code to disk." +
                "\nError: " + (e.getMessage() != null ? e.getMessage() : "No error message available."));
        alert.showAndWait();
    }
}